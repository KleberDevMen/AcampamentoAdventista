package br.com.kleberjunio.acampamentoadventista.fragmentos.roteiros;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.kleberjunio.acampamentoadventista.R;
import br.com.kleberjunio.acampamentoadventista.activity.enuns.Roteiros;
import br.com.kleberjunio.acampamentoadventista.modelos.roteiropojo.Roteiro;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoteiroSabadoFragment extends Fragment {


    public RoteiroSabadoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_roteiro_fragmento_sabado, container, false);

        //REFERENCIO COMPONENTES
        TextView conteudo = view.findViewById(R.id.conteudo_roteiro_sabado);
        ImageView imageView = view.findViewById(R.id.image_roteiro_sabado);

        Bundle dicionario = getArguments();

        if (dicionario.size() != 0) {
            //PEGO OBJETO NO DICIONARIO DE DADOS
            Roteiro r = (Roteiro) dicionario.getSerializable(Roteiros.SABADO.name());

            //SETO DADOS NA VIEW
            conteudo.setText(r.getContent().replace("\\n", "\n"));
            Picasso.get().load(r.getUrlImage()).into(imageView);
        }
        return view;
    }


}
