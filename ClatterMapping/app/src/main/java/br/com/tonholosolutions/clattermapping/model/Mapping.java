package br.com.tonholosolutions.clattermapping.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import br.com.tonholosolutions.clattermapping.orm.ClatterDatabase;

/**
 * Created on 12/11/2015.
 *
 * @author rafaeltonholo
 */
@Table(databaseName = ClatterDatabase.NAME)
public class Mapping extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    private long id;
    @Column
    private String endereco;
    @Column
    private double latitude;
    @Column
    private double longitude;
    @Column
    private double decibel;
    @Column
    private Date dataRegistro;

    public Mapping() {
        super();
    }

    public Mapping(double latitude, double longitude, double decibel) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.decibel = decibel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDecibel() {
        return decibel;
    }

    public void setDecibel(double decibel) {
        this.decibel = decibel;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }
}
