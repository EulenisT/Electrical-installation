package model.appareil;

/**
 * Structure immuable car on ne va jamais modifier un câble
 */
public record Cable(String code, short NbConducteurs, boolean terre, float section, String type) {

}
