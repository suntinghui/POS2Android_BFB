package com.bfb.pos.util;

import java.util.List;

import com.bfb.pos.model.CityModel;

public class Province {

    /**
     * province name
     */
    private String name;

    /**
     * province code
     */
    private int code;

    /**
     * province have cities
     */
    private List<CityModel> cities;

    public Province(String name, int code) {
        super();
        this.name = name;
        this.code = code;
    }

    public Province(String name, int code, List<CityModel> cities) {
        super();
        this.name = name;
        this.code = code;
        this.cities = cities;
    }

    /**
     * Getter of name
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of name
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of code
     * 
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * Setter of code
     * 
     * @param code
     *            the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Getter of cities
     * 
     * @return the cities
     */
    public List<CityModel> getCities() {
        return cities;
    }

    /**
     * Setter of cities
     * 
     * @param cities
     *            the cities to set
     */
    public void setCities(List<CityModel> cities) {
        this.cities = cities;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}