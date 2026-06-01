package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class Talisman extends Item {
    
    private String atributoBuff1;
    private double valorBuff1;

    private String atributoBuff2;
    private double valorBuff2;

    private String atributoDebuff;
    private double valorDebuff;

    public Talisman(String atributoBuff1, double valorBuff1, String atributoBuff2, double valorBuff2, String atributoDebuff, double valorDebuff, int id, String nome, String tipo, String descricao, int valorEmJoiasSombrias) {
        super(id, nome, tipo, descricao, valorEmJoiasSombrias);
        this.atributoBuff1 = atributoBuff1;
        this.valorBuff1 = valorBuff1;
        this.atributoBuff2 = atributoBuff2;
        this.valorBuff2 = valorBuff2;
        this.atributoDebuff = atributoDebuff;
        this.valorDebuff = valorDebuff;
    }
    
    public String getAtributoBuff1() {
        return atributoBuff1;
    }

    public void setAtributoBuff1(String atributoBuff1) {
        this.atributoBuff1 = atributoBuff1;
    }

    public double getValorBuff1() {
        return valorBuff1;
    }

    public void setValorBuff1(double valorBuff1) {
        this.valorBuff1 = valorBuff1;
    }

    public String getAtributoBuff2() {
        return atributoBuff2;
    }

    public void setAtributoBuff2(String atributoBuff2) {
        this.atributoBuff2 = atributoBuff2;
    }

    public double getValorBuff2() {
        return valorBuff2;
    }

    public void setValorBuff2(double valorBuff2) {
        this.valorBuff2 = valorBuff2;
    }

    public String getAtributoDebuff() {
        return atributoDebuff;
    }

    public void setAtributoDebuff(String atributoDebuff) {
        this.atributoDebuff = atributoDebuff;
    }

    public double getValorDebuff() {
        return valorDebuff;
    }

    public void setValorDebuff(double valorDebuff) {
        this.valorDebuff = valorDebuff;
    }
    
    
}