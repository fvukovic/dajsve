package com.example.filip.dajsve.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.filip.dajsve.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import entities.Favorit;
import entities.Ponuda;

import static android.graphics.Color.BLACK;

/**
 * Created by Helena on 23.11.2016..
 */

public class DetaljiPonudeFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {
    public CheckBox favoritCheckBox;
    Favorit trenutni;
    boolean statusFavoritPonuda;
    public Ponuda ponudaDohvacena;
    private ImageView ponudaSlika;
    private TextView ponudaNaziv;
    private TextView ponudaDescription;
    private TextView ponudaCijena;
    private TextView ponudaPopust;
    private TextView ponudaOriginal;
    private TextView ponudaDatum;
    private TextView ponudaUsteda;
    private TextView ponudaGrad;
    private TextView linkNaStranicu;
    private FrameLayout mapaPrikaz;
    private int position;
    private String name = "Map view";
    private com.google.android.gms.maps.MapFragment mapFragment;
    private GoogleMap map = null;
    private ImageButton gumbDodajUFavorite;

    Context context;
    private ImageView prozirnaSlika;
    private ScrollView scroll;
    private boolean ulazNaFragment=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.detalji_ponude_fragment, container, false);
        context = rootView.getContext();
        favoritCheckBox= (CheckBox)rootView.findViewById(R.id.checkBox);
        favoritCheckBox.setOnCheckedChangeListener(CheckBoxListener);
        ponudaSlika=(ImageView)rootView.findViewById(R.id.ponuda_image);
        ponudaNaziv=(TextView) rootView.findViewById(R.id.ponuda_name);
        linkNaStranicu = (TextView) rootView.findViewById(R.id.link_na_stranicu);
        //ponudaDescription=(TextView)rootView.findViewById(R.id.ponuda_description);
        ponudaCijena=(TextView)rootView.findViewById(R.id.ponuda_cijena);
        ponudaPopust=(TextView)rootView.findViewById(R.id.ponuda_popust);
        ponudaOriginal=(TextView)rootView.findViewById(R.id.ponuda_original);

        //ponudaDatum=(TextView)rootView.findViewById(R.id.ponuda_datum);
        //ponudaUsteda=(TextView)rootView.findViewById(R.id.ponuda_usteda);
        mapaPrikaz=(FrameLayout)rootView.findViewById(R.id.mapa_prikaz);
        //gumbDodajUFavorite=(ImageButton)rootView.findViewById(R.id.favoriti_dodavanje);

        Bundle bundle = getArguments();
        List<Favorit> favoriti= Favorit.getAll();
        ArrayList<Ponuda> listaDohvacena = bundle.getParcelableArrayList("ponuda");
        ponudaDohvacena = listaDohvacena.get(0);

        //Context context=ponudaSlika.getContext();
        Picasso.with(context).load(ponudaDohvacena.getURL()).into(ponudaSlika);
        ponudaNaziv.setText(ponudaDohvacena.getNaziv());
        //ponudaDescription.setText(ponudaDohvacena.getTekstPonude());
        ponudaCijena.setText(ponudaDohvacena.getCijena() + " kuna");
        ponudaPopust.setText((Integer.toString(ponudaDohvacena.getPopust()) + "%"));

        //strike preko stare cijene
        ponudaOriginal.setPaintFlags(ponudaOriginal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        ponudaOriginal.setText((Integer.toString(ponudaDohvacena.getCijenaOriginal()) + " kuna"));

        prozirnaSlika = (ImageView) rootView.findViewById(R.id.prozirnaslika);
        scroll = (ScrollView) rootView.findViewById(R.id.skrolanje);

        prozirnaSlika.setOnTouchListener(new View.OnTouchListener() {@Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    scroll.requestDisallowInterceptTouchEvent(true);
                    // Disable touch on transparent view
                    return false;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    scroll.requestDisallowInterceptTouchEvent(false);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    scroll.requestDisallowInterceptTouchEvent(true);
                    return false;

                default:
                    return true;
            }
        }
        });

        linkNaStranicu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(ponudaDohvacena.getUrlWeba());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

        //Provjera da li je favorit
        for(Favorit favorit : favoriti)

        {
            if(favorit.getHash().equals(ponudaDohvacena.getHash()))
            {
                statusFavoritPonuda=true;
                ulazNaFragment=true;
                favoritCheckBox.setChecked(true);
                trenutni=favorit;

            }
        }
        //dodavanje google karte u layout
        View v = inflater.inflate(com.example.map.R.layout.map_fragment, container, false);
        mapFragment = new com.google.android.gms.maps.MapFragment();
        mapFragment.getMapAsync(this);
        getActivity().getFragmentManager().beginTransaction().add(com.example.map.R.id.frame, mapFragment).commit();
        mapaPrikaz.setFocusable(true);
        mapaPrikaz.addView(v);

        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        try {
            if (ponudaDohvacena.getLongitude().contentEquals("nema") || ponudaDohvacena.getLatitude().contentEquals("nema")){
                //Ako nema koordinata v xml-u onda ime grada u kojem je ponuda pretvara u koordinate
                Geocoder geocoder = new Geocoder(context);
                String grad = ponudaDohvacena.getGrad();
                List<Address> adresa;
                adresa = geocoder.getFromLocationName(grad, 1);
                double latitude= adresa.get(0).getLatitude();
                double longitude= adresa.get(0).getLongitude();
                LatLng gradKoordinate = new LatLng(latitude, longitude);

                map.addMarker(new MarkerOptions()
                        .title(grad)
                        .snippet("tu neki opis ako treba?")
                        .position(gradKoordinate));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(gradKoordinate).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }else{
                //Uzima koordinate koje su dostupne v xml-u
                double ponudaLatitude = Double.parseDouble(ponudaDohvacena.getLatitude());
                double ponudaLongitude = Double.parseDouble(ponudaDohvacena.getLongitude());
                LatLng gradKoordinate = new LatLng(ponudaLatitude, ponudaLongitude);

                Geocoder geocoder = new Geocoder(context);
                List<Address> adresa;
                adresa = geocoder.getFromLocation(ponudaLatitude, ponudaLongitude,1);
                String cityName = adresa.get(0).getAddressLine(0);
                String stateName = adresa.get(0).getAddressLine(1);
                String countryName = adresa.get(0).getAddressLine(2);
                String Locality = adresa.get(0).getLocality();

                map.addMarker(new MarkerOptions()
                        .title("grad: " + Locality)
                        .snippet(cityName +"*"+ stateName+"*" + countryName)
                        .position(gradKoordinate));

                //Pozicionira i zumirana lokaciju od koordinata
                CameraPosition cameraPosition = new CameraPosition.Builder().target(gradKoordinate).zoom(13).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CompoundButton.OnCheckedChangeListener CheckBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            List<Favorit> favoriti= Favorit.getAll();
            if(statusFavoritPonuda==false){
            if(favoritCheckBox.isChecked() ){
                if( statusFavoritPonuda!=true) {
                    Favorit novi = new Favorit(favoriti.size(),ponudaDohvacena.getHash(), true, ponudaDohvacena.getId(), ponudaDohvacena.getTekstPonude(),
                            Integer.parseInt(ponudaDohvacena.getCijena()), ponudaDohvacena.getPopust()
                            , ponudaDohvacena.getCijenaOriginal(),ponudaDohvacena.getUrlSlike(), ponudaDohvacena.getUrlLogo(), ponudaDohvacena.getUrlWeba(),
                            ponudaDohvacena.getUsteda(), ponudaDohvacena.getKategorija(), ponudaDohvacena.getGrad(), ponudaDohvacena.getDatumPonude());
                    novi.save();
                }
            } else {
                Favorit.deleteFromWebUrl(ponudaDohvacena.getUrlWeba());



            }}
            statusFavoritPonuda=false;
        }

    };
}