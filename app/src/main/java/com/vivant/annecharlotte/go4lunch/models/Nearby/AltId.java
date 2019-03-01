package com.vivant.annecharlotte.go4lunch.models.Nearby;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Anne-Charlotte Vivant on 06/02/2019.
 */

    public class AltId {

        @SerializedName("place_id")
        @Expose
        private String placeId;
        @SerializedName("scope")
        @Expose
        private String scope;

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

    }
