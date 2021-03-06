package br.com.kleberjunio.acampamentoadventista.adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.kleberjunio.acampamentoadventista.R;
import br.com.kleberjunio.acampamentoadventista.modelos.youtubepojo.Item;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptadorVideo extends RecyclerView.Adapter<AdaptadorVideo.MyViewHolder> {

    private List<Item> videos;
    private Context context;

    //CONSTRUTOR
    public AdaptadorVideo(List<Item> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }











    //CRIO O VIEW HOLDER
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflar o layout da celula
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.celula_video, parent, false);
        return new MyViewHolder(view);
    }

    //OBJETO -> VIEW
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Item video = videos.get( position );
        holder.titulo.setText( video.getSnippet().getTitle() );

        String url = video.getSnippet().getThumbnails().getMedium().getUrl();
        Picasso.get().load(url).into(holder.capa);

        try {
            Picasso.get().load(video.getSnippet().getThumbnails().getMedium().getUrl()).into(holder.capa);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //RETORNA OS NUMERO DE CELULAS
    @Override
    public int getItemCount() {
        return videos.size();
    }






















    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo;
        ImageView capa;

        public MyViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTituloVideo);
            capa = itemView.findViewById(R.id.imageCapaVideo);
        }
    }

}

