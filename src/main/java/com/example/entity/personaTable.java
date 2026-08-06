/**
 *      public int ID_column { get; set; }
        public string NombreCompleto { get; set; }
        public string ProfesionOficio { get; set; }
        public string Ciudad { get; set; }
*/
package com.example.entity;

public class personaTable {
    //
    private long id_Column;
    private String ciudad;
    private String nombreCompleto;
    // private String profesionOficio;

    public personaTable(long id_Column, String ciudad, String nombreCompleto /* ,String profesionOficio */) {
        this.id_Column = id_Column;
        this.ciudad = ciudad;
        this.nombreCompleto = nombreCompleto;
        // this.profesionOficio = profesionOficio;

    }

    public long getId_Column() {
        return id_Column;
    }

    public void setId_Column(long id_Column) {
        this.id_Column = id_Column;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    /*
     * /
     * public String getProfesionOficio() {
     * return profesionOficio;
     * }
     * 
     * public void setProfesionOficio(String profesionOficio) {
     * this.profesionOficio = profesionOficio;
     * }
     */
}
