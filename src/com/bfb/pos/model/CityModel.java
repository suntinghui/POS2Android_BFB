package com.bfb.pos.model;

public class CityModel {

    /**
     * city name
     */
    private String name;

    /**
     * province code;
     */
    private int province_code;

    /**
     * city code
     */
    private int code;

    public CityModel(String name, int province_code, int code) {
        super();
        this.name = name;
        this.province_code = province_code;
        this.code = code;
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
     * Getter of province_code
     * 
     * @return the province_code
     */
    public int getProvince_code() {
        return province_code;
    }

    /**
     * Setter of province_code
     * 
     * @param province_code
     *            the province_code to set
     */
    public void setProvince_code(int province_code) {
        this.province_code = province_code;
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
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}