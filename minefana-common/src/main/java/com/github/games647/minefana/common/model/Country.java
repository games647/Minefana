package com.github.games647.minefana.common.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Country {

    private final String isoCode;
    private final Integer geoNameId;
    private final String name;

    public Country(String isoCode, Integer geoNameId, String name) {
        this.isoCode = isoCode;
        this.geoNameId = geoNameId;
        this.name = name;
    }

    public static Country of(JsonElement jsonElement) {
        JsonObject object = jsonElement.getAsJsonObject();
        JsonObject country = object.getAsJsonObject("country");

        int geoNameId = country.getAsJsonPrimitive("geoname_id").getAsInt();
        String isoCode = country.getAsJsonPrimitive("iso_code").getAsString();
        String name = country.getAsJsonObject("names").get("en").getAsString();
        return new Country(isoCode, geoNameId, name);
    }

    public String getIsoCode() {
        return isoCode;
    }

    public Integer getGeoNameId() {
        return geoNameId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Country{" +
                "isoCode='" + isoCode + '\'' +
                ", geoNameId=" + geoNameId +
                ", name='" + name + '\'' +
                '}';
    }
}
