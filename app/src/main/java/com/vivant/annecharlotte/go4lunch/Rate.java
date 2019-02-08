package com.vivant.annecharlotte.go4lunch;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Anne-Charlotte Vivant on 08/02/2019.
 */
public class Rate {

    // Aucune étoile en dessous de 2.5, 1 étoile entre 2.6 et 3.5, 2 étoiles entre 3.6 et 4.5, 3 étoiles au-dessus

public Rate(double rate, ImageView star1, ImageView star2, ImageView star3) {
    int rate_int = (int) Math.round(rate);

    switch (rate_int) {
        case 0:
            star1.setVisibility(View.GONE);
            star2.setVisibility(View.GONE);
            star3.setVisibility(View.GONE);
            break;
        case 1:
            star1.setVisibility(View.GONE);
            star2.setVisibility(View.GONE);
            star3.setVisibility(View.GONE);
            break;
        case 2:
            star1.setVisibility(View.GONE);
            star2.setVisibility(View.GONE);
            star3.setVisibility(View.GONE);
            break;
        case 3:
            star1.setVisibility(View.GONE);
            star2.setVisibility(View.GONE);
            star3.setVisibility(View.VISIBLE);
            break;
        case 4:
            star1.setVisibility(View.GONE);
            star2.setVisibility(View.VISIBLE);
            star3.setVisibility(View.VISIBLE);
            break;
        case 5:
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.VISIBLE);
            star3.setVisibility(View.VISIBLE);
            break;
    }
}
}
