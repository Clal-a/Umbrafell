package com.ced.umbrafell.model;

/**
 *
 * @authors Cesar & Danilo
 */
public class Talisman extends Item {

    private int idTalisma;
    private int idItem;

    private String atributoBuff1;
    private double valorBuff1;

    private String atributoBuff2;
    private double valorBuff2;

    private String atributoDebuff;
    private double valorDebuff;

    public Talisman() {
    }

    public Talisman(
            int idTalisma,
            int idItem,
            String nome,
            String tipo,
            String descricao,
            int valorJoiasSombrias,
            String atributoBuff1,
            double valorBuff1,
            String atributoBuff2,
            double valorBuff2,
            String atributoDebuff,
            double valorDebuff
    ) {
        super(idItem, nome, tipo, descricao, valorJoiasSombrias);
        this.idTalisma = idTalisma;
        this.idItem = idItem;
        this.atributoBuff1 = atributoBuff1;
        this.valorBuff1 = valorBuff1;
        this.atributoBuff2 = atributoBuff2;
        this.valorBuff2 = valorBuff2;
        this.atributoDebuff = atributoDebuff;
        this.valorDebuff = valorDebuff;
    }

    public int getIdTalisma() {
        return idTalisma;
    }

    public void setIdTalisma(int idTalisma) {
        this.idTalisma = idTalisma;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
        setId(idItem);
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