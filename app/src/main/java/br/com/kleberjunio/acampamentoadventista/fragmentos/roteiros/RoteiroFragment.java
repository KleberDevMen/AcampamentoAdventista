package br.com.kleberjunio.acampamentoadventista.fragmentos.roteiros;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.kleberjunio.acampamentoadventista.R;

public abstract class RoteiroFragment extends Fragment {


    public RoteiroFragment() {
        // Required empty public constructor
    }

    protected TextView titulo;
    protected TextView conteudo;
    protected Bundle dicionario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roteiro_fragmento_sexta, container, false);

    }


}
