package hr.foi.air.dajsve.Fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import hr.foi.air.dajsve.Activities.MainActivity;
import hr.foi.air.dajsve.Adapters.RVAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entities.Kategorija;
import entities.OmiljenaKategorija;
import entities.Ponuda;

/**
 * Created by Filip on 28.10.2016..
 */

public class MojeKategorijeFragment extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rv;
    public RVAdapter adapter;
    public SwipeRefreshLayout mSwipeRefreshLayout;
     public String[] kategorije ;
    public List<String> oznaceneKategorije;
    public  boolean[] oznaceneKategorijeDialog ;
    public AlertDialog ad;
    Button urediOmiljeneKategorije;
    public FloatingActionButton fab;
    public String[] opcijeSortiranja;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(hr.foi.air.dajsve.R.layout.moje_kategorije_fragment, container, false);
        rv = (RecyclerView) rootView.findViewById(hr.foi.air.dajsve.R.id.rv);
        final AlertDialog.Builder ad =  new AlertDialog.Builder(getActivity());
        fab = (FloatingActionButton) rootView.findViewById(hr.foi.air.dajsve.R.id.fab_sve_ponude);
        opcijeSortiranja = new String[3];
        opcijeSortiranja[0] = "Cijena - uzlazno";
        opcijeSortiranja[1] = "Cijena - silazno";
        opcijeSortiranja[2] = "Popust";

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        final TextView textView = (TextView) rootView.findViewById(hr.foi.air.dajsve.R.id.textView3);
        int i=0;
        kategorije = new String[Kategorija.getAll().size()];
        oznaceneKategorijeDialog = new boolean[Kategorija.getAll().size()];
        urediOmiljeneKategorije = (Button) rootView.findViewById(hr.foi.air.dajsve.R.id.uredi_pregled_button);



        for(Kategorija kategorija : Kategorija.getAll())
        {
            kategorije[i]= kategorija.getNaziv();
            i++;
        }
        for(int j= 0;j<kategorije.length;j++)
        {
            for(OmiljenaKategorija a : OmiljenaKategorija.getAll())
            {
                System.out.print("PRINT : "+a.getNaziv().equals(kategorije[j]));
                if(a.getNaziv().equals(kategorije[j]))
                {
                    oznaceneKategorijeDialog[j]=true;
                }
            }
        }
        ad.setTitle("ODABERITE KATEGORIJU");
        ad.setMultiChoiceItems(kategorije, oznaceneKategorijeDialog, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                Toast.makeText(getContext(), kategorije[which], Toast.LENGTH_SHORT).show();
                for(boolean aa : oznaceneKategorijeDialog)
                {
                    System.out.println(aa);
                }
            }
        });
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OmiljenaKategorija.deleteAll();
                for(int i = 0 ; i<kategorije.length;i++)
                {
                    if(oznaceneKategorijeDialog[i]==true) {
                        OmiljenaKategorija novi = new OmiljenaKategorija(kategorije[i]);
                        novi.save();
                        System.out.println(kategorije[i]);
                    }
                }
                List<Ponuda> ponudePoKategoriji = new ArrayList<Ponuda>();
                for (OmiljenaKategorija a : OmiljenaKategorija.getAll()){
                    ponudePoKategoriji.addAll(Ponuda.getByFavoriteCategory(a.getNaziv()));
                }

                System.out.println("Ispis ponuda po omiljenim kategorijama: ");
                for(Ponuda ponuda : ponudePoKategoriji){
                    System.out.println(ponuda.getNaziv());
                    System.out.println("         " + ponuda.getKategorija());
                }

                RVAdapter adapter = new RVAdapter(ponudePoKategoriji,getContext());
                rv.setAdapter(adapter);
                if(adapter.getItemCount() == 0) {
                    textView.setText("Ne postoje ponude za odabrane omiljene kategorije");
                }
                else
                {
                    textView.setText("");
                }

            }
        });

        ad.setItems(kategorije,null);



        urediOmiljeneKategorije.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.show();
            }
        });



        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(hr.foi.air.dajsve.R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        rv.setLayoutManager(llm);
        if(OmiljenaKategorija.getAll().isEmpty()) {
            textView.setText("NISTE ODABRALI KATEGORIJU");
        }
        else
        {
            textView.setText("");
        }
        ArrayList<Ponuda> listaOmiljenihPonuda = new ArrayList<Ponuda>();

        List<OmiljenaKategorija> listaOmiljenihKategorija = OmiljenaKategorija.getAll();
        for(OmiljenaKategorija omiljenaKategorija : listaOmiljenihKategorija){
            listaOmiljenihPonuda.addAll(Ponuda.getByFavoriteCategory(omiljenaKategorija.getNaziv()));
        }
        Collections.shuffle(listaOmiljenihPonuda);

        RVAdapter adapter = new RVAdapter(listaOmiljenihPonuda,getContext());
        rv.setAdapter(adapter);

        return rootView;
    }

    class task extends AsyncTask<Void , Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            mSwipeRefreshLayout.setRefreshing(false);
            System.out.println("USAO SAM U POST IN");

            ArrayList<Ponuda> listaOmiljenihPonuda = new ArrayList<Ponuda>();

            List<OmiljenaKategorija> listaOmiljenihKategorija = OmiljenaKategorija.getAll();
            for(OmiljenaKategorija omiljenaKategorija : listaOmiljenihKategorija){
                listaOmiljenihPonuda.addAll(Ponuda.getByFavoriteCategory(omiljenaKategorija.getNaziv()));
            }

            Collections.shuffle(listaOmiljenihPonuda);
            RVAdapter adapter = new RVAdapter(listaOmiljenihPonuda,getContext());
            rv.setAdapter(adapter);
            super.onPostExecute(aVoid);
        }


        @Override
        protected Void doInBackground(Void... params) {
            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }
            ((MainActivity)getActivity()).loadData();
            return null;

        }
    }

    @Override
    public void onRefresh() {
        new task().execute();
    }
}