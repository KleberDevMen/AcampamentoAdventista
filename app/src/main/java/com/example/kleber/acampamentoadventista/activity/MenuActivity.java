package com.example.kleber.acampamentoadventista.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.kleber.acampamentoadventista.R;
import com.example.kleber.acampamentoadventista.modelos.informepojo.Informe;
import com.example.kleber.acampamentoadventista.modelos.musicapojo.Musica;
import com.example.kleber.acampamentoadventista.modelos.roteiropojo.Roteiro;
import com.example.kleber.acampamentoadventista.modelos.url_playlist_videos_pojo.UrlPlaylistVideo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    //WEB_SERVICE: MUSICAS
    private String urlMusicas = "https://fierce-inlet-45074.herokuapp.com/musics.json";

    //WEB_SERVICE: ROTEIROS
    private String urlRoteiros = "https://fierce-inlet-45074.herokuapp.com/scripts.json";

    //WEB_SERVICE: INFORMES
    private String urlInformes = "https://fierce-inlet-45074.herokuapp.com/infos.json";

    //WEB_SERVICE: LINK
    private String urlPlaylistVideos = "https://fierce-inlet-45074.herokuapp.com/link_playlist_videos.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        buscaDadosESalvaNaBaseLocal();
    }

    public void btnMusicas(View botao) {
        Intent intencao = new Intent(this, ListaMusicasActivity.class);
        this.startActivity(intencao);
    }

    public void btnRoteiro(View botao) {
        Intent intencao = new Intent(this, RoteirosActivity.class);
        this.startActivity(intencao);
    }

    public void btnNews(View botao) {
        Intent intencao = new Intent(this, ListaVideosActivity.class);
        this.startActivity(intencao);
    }

    public void btnMeditacao(View botao) {
        Intent intencao = new Intent(this, InformesActivity.class);
        this.startActivity(intencao);
    }

    //INFLANDO ITENS_DE_ MENU NA ACTION_BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // add o menu tres pontinhos
        inflater.inflate(R.menu.menu_tres_pontinhos, menu);
        return true;
    }

    //OUVINTE DOS BOTOES DO MENU 3 PONTINHOS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.contatos:
                //CHAMA A TELA DE CONTATOS
                this.startActivity(new Intent(this, ContatosActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    //BUSCA DADOS NO WEBSERVICE E SALVA NO SQLITE
    public void buscaDadosESalvaNaBaseLocal() {

        //SQLite
        try {
            //CRIA/ABRE BANCO LOCAL
            SQLiteDatabase bancoDeDados = openOrCreateDatabase("app"
                    , MODE_PRIVATE, null);

            //-------------------- BAIXA ROTEIROS -----------------------
            BuscaRoteiros buscaRoteiros = new BuscaRoteiros(this, bancoDeDados);
            buscaRoteiros.execute(urlRoteiros);

            //-------------------- BAIXA MUSICAS -----------------------
            BuscaMuscia buscaMusica = new BuscaMuscia(this, bancoDeDados);
            buscaMusica.execute(urlMusicas);

            //-------------------- BAIXA INFORMATIVOS -----------------------
            BuscaInformes buscaInformes = new BuscaInformes(this, bancoDeDados);
            buscaInformes.execute(urlInformes);

            //-------------------- ATUALIZA O LINK DA PLAYLIST DO YOUTUBE -----------------------
            BuscaLink buscaLink = new BuscaLink(this, bancoDeDados);
            buscaLink.execute(urlPlaylistVideos);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //TREAD QUE BUSCA OS ROTEIROS
    class BuscaRoteiros extends AsyncTask<String, Void, List<Roteiro>> {

        private AppCompatActivity activity = null;
        private SQLiteDatabase bancoDeDados;

        public BuscaRoteiros(AppCompatActivity activity, SQLiteDatabase bancoDeDados) {
            this.activity = activity;
            this.bancoDeDados = bancoDeDados;
        }

        @Override
        protected List<Roteiro> doInBackground(String... strings) {

            String stringUrl = strings[0];
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            StringBuffer buffer = null;

            try {

                URL url = new URL(stringUrl);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

                // Recupera os dados em Bytes
                inputStream = conexao.getInputStream();

                //inputStreamReader lê os dados em Bytes e decodifica para caracteres
                inputStreamReader = new InputStreamReader(inputStream);

                //Objeto utilizado para leitura dos caracteres do InpuStreamReader
                BufferedReader reader = new BufferedReader(inputStreamReader);
                buffer = new StringBuffer();
                String linha = "";

                while ((linha = reader.readLine()) != null) {
                    buffer.append(linha);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();

            List<Roteiro> roteiros = null;

            Type collectionType = new TypeToken<List<Roteiro>>() {
            }.getType();

            try {
                roteiros = gson.fromJson(buffer.toString(), collectionType);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (roteiros == null)
                return null;
            else
                return roteiros;
        }

        @Override
        protected void onPostExecute(List<Roteiro> roteiros) {
            super.onPostExecute(roteiros);

            if (roteiros == null) {
                bancoDeDados.execSQL("DROP TABLE IF EXISTS roteiros");

                //CRIA TABELA
                bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS roteiros(id INTEGER, titulo VARCHAR, conteudo VARCHAR, url_imagem VARCHAR )");
//
//                //APAGA ROTEIROS
//                bancoDeDados.execSQL("DELETE FROM roteiros;");

                for (Roteiro r : roteiros) {
                    //INSERE MUSICA
                    bancoDeDados.execSQL("INSERT INTO roteiros(id, titulo, conteudo, url_imagem) VALUES('" + r.getId() + "', '" + r.getTitle() + "', '" + r.getContent() + "', '" + r.getUrlImage() + "') ");
                }
            }
        }
    }

    //TREAD QUE BUSCA A LETRA DAS MUSICAS
    class BuscaMuscia extends AsyncTask<String, Void, List<Musica>> {

        private AppCompatActivity activity = null;
        private SQLiteDatabase bancoDeDados;

        public BuscaMuscia(AppCompatActivity activity, SQLiteDatabase bancoDeDados) {
            this.activity = activity;
            this.bancoDeDados = bancoDeDados;
        }

        @Override
        protected List<Musica> doInBackground(String... strings) {

            String stringUrl = strings[0];
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            StringBuffer buffer = null;

            try {

                URL url = new URL(stringUrl);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

                // Recupera os dados em Bytes
                inputStream = conexao.getInputStream();

                //inputStreamReader lê os dados em Bytes e decodifica para caracteres
                inputStreamReader = new InputStreamReader(inputStream);

                //Objeto utilizado para leitura dos caracteres do InpuStreamReader
                BufferedReader reader = new BufferedReader(inputStreamReader);
                buffer = new StringBuffer();
                String linha = "";

                while ((linha = reader.readLine()) != null) {
                    buffer.append(linha);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();

            List<Musica> musicas = null;

            Type collectionType = new TypeToken<List<Musica>>() {
            }.getType();

            try {
                musicas = gson.fromJson(buffer.toString(), collectionType);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (musicas == null)
                return null;
            else
                return musicas;
        }

        @Override
        protected void onPostExecute(List<Musica> musicas) {
            super.onPostExecute(musicas);

            if (musicas == null) {
                bancoDeDados.execSQL("DROP TABLE IF EXISTS musicas");

                //CRIA TABELA
                bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS musicas ( titulo VARCHAR, artista VARCHAR, letra VARCHAR, id INTEGER, url_imagem VARCHAR)");

//                //APAGA MUSICAS
//                bancoDeDados.execSQL("DELETE FROM musicas;");

                for (Musica m : musicas) {
                    //INSERE MUSICA
                    bancoDeDados.execSQL("INSERT INTO musicas(titulo, artista, letra, id, url_imagem) VALUES('" + m.getTitle() + "', '" + m.getArtist() + "', '" + m.getLyric() + "', '" + m.getId() + "','" + m.getUrlImage() + "') ");
                }
            }
        }
    }

    //TREAD QUE BUSCA OS INFORMATIVOS
    class BuscaInformes extends AsyncTask<String, Void, List<Informe>> {

        private AppCompatActivity activity = null;
        private SQLiteDatabase bancoDeDados;

        public BuscaInformes(AppCompatActivity activity, SQLiteDatabase bancoDeDados) {
            this.activity = activity;
            this.bancoDeDados = bancoDeDados;
        }

        @Override
        protected List<Informe> doInBackground(String... strings) {

            String stringUrl = strings[0];
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            StringBuffer buffer = null;

            try {

                URL url = new URL(stringUrl);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

                // Recupera os dados em Bytes
                inputStream = conexao.getInputStream();

                //inputStreamReader lê os dados em Bytes e decodifica para caracteres
                inputStreamReader = new InputStreamReader(inputStream);

                //Objeto utilizado para leitura dos caracteres do InpuStreamReader
                BufferedReader reader = new BufferedReader(inputStreamReader);
                buffer = new StringBuffer();
                String linha = "";

                while ((linha = reader.readLine()) != null) {
                    buffer.append(linha);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();

            List<Informe> informes = null;

            Type collectionType = new TypeToken<List<Informe>>() {
            }.getType();

            try {
                informes = gson.fromJson(buffer.toString(), collectionType);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (informes == null)
                return null;
            else
                return informes;
        }

        @Override
        protected void onPostExecute(List<Informe> informes) {
            super.onPostExecute(informes);

            if (informes == null) {
                bancoDeDados.execSQL("DROP TABLE IF EXISTS informes");

                //CRIA TABELA
                bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS informes(id INTEGER, titulo VARCHAR, conteudo VARCHAR, url_imagem VARCHAR )");

//                //APAGA ROTEIROS
//                bancoDeDados.execSQL("DELETE FROM informes;");

                for (Informe informe : informes) {
                    //INSERE MUSICA
                    bancoDeDados.execSQL("INSERT INTO informes(id, titulo, conteudo, url_imagem) VALUES('" + informe.getId() + "', '" + informe.getTitle() + "', '" + informe.getContent() + "', '" + informe.getUrlImage() + "') ");
                }
            }
        }
    }

    //TREAD QUE BUSCA E ATUALIZA O LINK
    class BuscaLink extends AsyncTask<String, Void, String> {

        private AppCompatActivity activity = null;
        private SQLiteDatabase bancoDeDados;

        public BuscaLink(AppCompatActivity activity, SQLiteDatabase bancoDeDados) {
            this.activity = activity;
            this.bancoDeDados = bancoDeDados;
        }

        @Override
        protected String doInBackground(String... strings) {

            String stringUrl = strings[0];
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            StringBuffer buffer = null;

            try {

                URL url = new URL(stringUrl);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

                // Recupera os dados em Bytes
                inputStream = conexao.getInputStream();

                //inputStreamReader lê os dados em Bytes e decodifica para caracteres
                inputStreamReader = new InputStreamReader(inputStream);

                //Objeto utilizado para leitura dos caracteres do InpuStreamReader
                BufferedReader reader = new BufferedReader(inputStreamReader);
                buffer = new StringBuffer();
                String linha = "";

                while ((linha = reader.readLine()) != null) {
                    buffer.append(linha);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();

            List<UrlPlaylistVideo> urlPlaylistVideos = null;

            Type collectionType = new TypeToken<List<UrlPlaylistVideo>>() {
            }.getType();

            try {
                urlPlaylistVideos = gson.fromJson(buffer.toString(), collectionType);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (urlPlaylistVideos == null)
                return null;
            else
                return urlPlaylistVideos.get(0).getLink();
        }

        @Override
        protected void onPostExecute(String linkPlaylistVideos) {
            super.onPostExecute(linkPlaylistVideos);

            if (linkPlaylistVideos != null) {
                bancoDeDados.execSQL("DROP TABLE IF EXISTS linkplaylistvideos");

                //CRIA TABELA
                bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS linkplaylistvideos(link VARCHAR)");
//
//                //APAGA TABELA
//                bancoDeDados.execSQL("DELETE FROM linkplaylistvideos;");

                //INSERE NA TABELA
                bancoDeDados.execSQL("INSERT INTO linkplaylistvideos(link) VALUES('" + linkPlaylistVideos + "')");
            }
        }
    }
}
