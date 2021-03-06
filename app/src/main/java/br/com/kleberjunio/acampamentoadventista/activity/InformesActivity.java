package br.com.kleberjunio.acampamentoadventista.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.IDNA;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.kleberjunio.acampamentoadventista.R;
import br.com.kleberjunio.acampamentoadventista.activity.enuns.Informes;
import br.com.kleberjunio.acampamentoadventista.adaptadores.AdaptadorDePaginas;
import br.com.kleberjunio.acampamentoadventista.fragmentos.informes.MandamentosFragment;
import br.com.kleberjunio.acampamentoadventista.fragmentos.informes.MensagemFragment;
import br.com.kleberjunio.acampamentoadventista.fragmentos.informes.OQueLevarFragment;
import br.com.kleberjunio.acampamentoadventista.fragmentos.informes.RegrasFragment;
import br.com.kleberjunio.acampamentoadventista.fragmentos.roteiros.RoteiroDomingoFragment;
import br.com.kleberjunio.acampamentoadventista.fragmentos.roteiros.RoteiroSabadoFragment;
import br.com.kleberjunio.acampamentoadventista.fragmentos.roteiros.RoteiroSegundaFragment;
import br.com.kleberjunio.acampamentoadventista.fragmentos.roteiros.RoteiroSextaFragment;
import br.com.kleberjunio.acampamentoadventista.fragmentos.roteiros.RoteiroTercaFragment;
import br.com.kleberjunio.acampamentoadventista.modelos.informepojo.Informe;

public class InformesActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    //ADAPTER PARA OS FRAGMENTS
    AdaptadorDePaginas adapter;

    //ROTEIROS
    private MandamentosFragment mandamentosFragment;
    private MensagemFragment mensagemFragment;
    private OQueLevarFragment oQueLevarFragment;
    private RegrasFragment regrasFragment;

    //DICIONARIO
    private Bundle dicionario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes);

        inicializaComponentes();
        configuraToolbar();

        //RECUPERA AS INFORMAÇOES DO BANCO
        recuperarInformacoes();

        // CRIO O ADAPTER
        adapter = new AdaptadorDePaginas(getSupportFragmentManager());

        // SETO OS FRAGMENTOS NO ADPTER
        adapter.AddFragment(oQueLevarFragment, getString(R.string.o_que_levar));
        adapter.AddFragment(regrasFragment, getString(R.string.regras));
        adapter.AddFragment(mensagemFragment, getString(R.string.mensagem));
        adapter.AddFragment(mandamentosFragment, getString(R.string.mandamentos));

        // SETO O ADAPTER NA VIEW PAGE
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    //RECUPERA AS INFORMAÇOES DO BANCO
    private void recuperarInformacoes() {
        //SQLite
        try {
            //ABRIR BANCO
            SQLiteDatabase bancoDeDados = openOrCreateDatabase("app"
                    , MODE_PRIVATE, null);

            //RECUPERAR
            Cursor cursor = bancoDeDados.rawQuery("SELECT titulo, conteudo, url_imagem FROM informes ORDER BY id ASC", null);

            //INDICES DA TABELA
            int indiceTitulo = cursor.getColumnIndex("titulo");
            int indiceConteudo = cursor.getColumnIndex("conteudo");
            int indiceUrlImagem = cursor.getColumnIndex("url_imagem");

            //PERCORE TABELA
            int i = 0;
            cursor.moveToFirst();
            while (cursor != null) {
                inicializaInformeFragment(i
                        , dicionario
                        , cursor.getString(indiceTitulo)
                        , cursor.getString(indiceConteudo)
                        , cursor.getString(indiceUrlImagem));

                cursor.moveToNext();
                i++;

                //////////// TIRAR!!!!
                //QTD DE FRAGMENTOS PRONTOS
                if (i == 4) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configuraToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(1);
        }
    }
    private void inicializaComponentes() {
        tabLayout = findViewById(R.id.tablayout_informes);
        viewPager = findViewById(R.id.pager_informes);
        dicionario = new Bundle();
    }


    //INICIALIZO O FRAGMENTO ROTEIRO COM DADOS DO SQLITE
    private void inicializaInformeFragment(int posicao, Bundle dicionario, String titulo, String conteudo, String url_imagem) {

        Informe informe = null;

        switch (posicao){

            case 0:
                informe = new Informe(titulo, conteudo, url_imagem);
                dicionario.putSerializable(Informes.MANDAMENTOS.name(), informe);
                mandamentosFragment = new MandamentosFragment();
                mandamentosFragment.setArguments(dicionario);
                break;
            case 1:
                informe = new Informe(titulo, conteudo, url_imagem);
                dicionario.putSerializable(Informes.MENSAGEM.name(), informe);
                mensagemFragment = new MensagemFragment();
                mensagemFragment.setArguments(dicionario);
                break;
            case 2:
                informe = new Informe(titulo, conteudo, url_imagem);
                dicionario.putSerializable(Informes.O_QUE_LEVAR.name(), informe);
                oQueLevarFragment = new OQueLevarFragment();
                oQueLevarFragment.setArguments(dicionario);
                break;
            case 3:
                informe = new Informe(titulo, conteudo, url_imagem);
                dicionario.putSerializable(Informes.REGRAS.name(), informe);
                regrasFragment = new RegrasFragment();
                regrasFragment.setArguments(dicionario);
                break;
            case 4:
                break;
        }

    }
}
