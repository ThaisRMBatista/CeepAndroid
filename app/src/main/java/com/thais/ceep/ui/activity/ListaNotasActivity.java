package com.thais.ceep.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.thais.ceep.R;
import com.thais.ceep.dao.NotasDAO;
import com.thais.ceep.model.Nota;
import com.thais.ceep.ui.recyclerview.adapter.ListaNotaAdapter;
import com.thais.ceep.ui.recyclerview.helper.callback.NotaItemTouchHelperCallback;

import java.util.List;

import static com.thais.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static com.thais.ceep.ui.activity.NotaActivityConstantes.CHAVE_POSICAO;
import static com.thais.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_NOTA;
import static com.thais.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_NOTA;
import static com.thais.ceep.ui.activity.NotaActivityConstantes.POSICAO_INVALIDA;

public class ListaNotasActivity extends AppCompatActivity {


    public static final String TITULO_APP_BAR = "Notas";
    private ListaNotaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notas);
        setTitle(TITULO_APP_BAR);

        List<Nota> todasNotas = pegaTodasNotas();

        configuraRecyclerView(todasNotas);

        configuraBotaoInsereNota();
    }

    private void configuraBotaoInsereNota() {
        TextView botaoInsereNota = findViewById(R.id.lista_notas_insere_notas);
        botaoInsereNota.setOnClickListener(v -> vaiParaFormularioNotaActivityInserir());
    }

    private void vaiParaFormularioNotaActivityInserir() {
        Intent intentFormularioNota = new Intent(ListaNotasActivity.this, FormularioNotaActivity.class);
        startActivityForResult(intentFormularioNota, CODIGO_REQUISICAO_INSERE_NOTA);
    }

    private List<Nota> pegaTodasNotas() {
        NotasDAO dao = new NotasDAO();
        return dao.todos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isResultadoInsereNota(requestCode,data)){
            if (resultadoOk(resultCode)) {
                assert data != null;
                Nota notaRecebida = (Nota) data.getSerializableExtra(CHAVE_NOTA);
                adiciona(notaRecebida);
            }
        }

        if (isResultadoAlteraNota(requestCode, data)) {
            if (resultadoOk(resultCode)) {
                assert data != null;
                Nota notaRecebida = (Nota) data.getSerializableExtra(CHAVE_NOTA);
                int posicaoRecebida = data.getIntExtra(CHAVE_POSICAO, POSICAO_INVALIDA);
                if (isPosicaoValida(posicaoRecebida)) {
                    altera(notaRecebida, posicaoRecebida);
                }
            }
        }
    }

    private void altera(Nota nota, int posicao) {
        new NotasDAO().altera(posicao, nota);
        adapter.altera(posicao, nota);
    }

    private boolean isPosicaoValida(int posicaoRecebida) {
        return posicaoRecebida > -1;
    }

    private boolean isResultadoAlteraNota(int requestCode, @Nullable Intent data) {
        return isCodigoRequisicaoAlteraNota(requestCode) && hasNota(data);
    }

    private boolean isCodigoRequisicaoAlteraNota(int requestCode) {
        return requestCode == CODIGO_REQUISICAO_ALTERA_NOTA;
    }

    private void adiciona(Nota nota) {
        new NotasDAO().insere(nota);
        adapter.adiciona(nota);
    }

    private boolean isResultadoInsereNota(int requestCode, @Nullable Intent data) {
        return isCodigoRequisicaoInsereNota(requestCode) && hasNota(data);
    }

    private boolean hasNota(@Nullable Intent data) {
        return data!= null && data.hasExtra(CHAVE_NOTA);
    }

    private boolean resultadoOk(int resultCode) {
        return resultCode == Activity.RESULT_OK;
    }

    private boolean isCodigoRequisicaoInsereNota(int requestCode) {
        return requestCode == CODIGO_REQUISICAO_INSERE_NOTA;
    }

    private void configuraRecyclerView(List<Nota> todasNotas) {
        RecyclerView listaNotas = findViewById(R.id.lista_notas_recyclerview);
        configuraAdapter(todasNotas, listaNotas);
        configuraItemTouchHelper(listaNotas);
    }

    private void configuraItemTouchHelper(RecyclerView listaNotas) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new NotaItemTouchHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(listaNotas);
    }

    private void configuraAdapter(List<Nota> todasNotas, RecyclerView listaNotas) {
        adapter = new ListaNotaAdapter(this, todasNotas);
        listaNotas.setAdapter(adapter);
        adapter.setOnItemClickListener(this::vaiParaFormularioNotaActivityAltera);
    }

    private void vaiParaFormularioNotaActivityAltera(Nota nota, int posicao) {
        Intent abreFormularioComNota = new Intent(ListaNotasActivity.this, FormularioNotaActivity.class);
        abreFormularioComNota.putExtra(CHAVE_NOTA, nota);
        abreFormularioComNota.putExtra(CHAVE_POSICAO, posicao);
        startActivityForResult(abreFormularioComNota, CODIGO_REQUISICAO_ALTERA_NOTA);
    }
}