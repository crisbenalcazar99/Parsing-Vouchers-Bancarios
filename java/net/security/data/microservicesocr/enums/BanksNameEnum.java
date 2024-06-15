package net.security.data.microservicesocr.enums;


import java.util.HashMap;
import java.util.Map;

public enum BanksNameEnum {
    REF_BANCO_PICHINCHA("Banco Pichincha"),
    REF_PRODUBANCO("Produbanco"),
    REF_BANCO_GUAYAQUIL("Banco Guayaquil"),
    REF_BANCO_BOLIVARIANO("Banco Bolivariano"),
    REF_BANCO_PACIFICO("Banco Pacifico"),
    REF_BANCO_INTERNACIONAL("Banco Internacional"),
    REF_SECURITY_DATA("Security Data"),
    REF_JEP("Cooperativa de Ahorro y Crédito JEP"),
    REF_BANCO_AUSTRO("Banco del Austro"),
    REF_COOP_AZUAYO("Cooperativa de Ahorro y Credito Jardin Azuayo"),

    COMP_BANCO_PICHINCHA("pichincha"),
    COMP_PRODUBANCO("produbanco"),
    COMP_BANCO_GUAYAQUIL("guayaquil"),
    COMP_BANCO_BOLIVARIANO("bolivariano"),
    COMP_BANCO_PACIFICO("pacifico"),
    COMP_BANCO_INTERNACIONAL("internacional"),
    COMP_SECURITY_DATA("security data"),
    COMP_SECURITY_DATA_2("security"),
    COMP_JEP("jep"),
    COMP_BANCO_AUSTRO("austro"),
    COMP_COOP_AZUAYO("azuayo");

    private String value;

    BanksNameEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static Map<String, String> standardNames() {
        Map<String, String> SET_NAME_STANDARD = new HashMap<>();
        BanksNameEnum[] refValues = {
                REF_BANCO_PICHINCHA, REF_PRODUBANCO, REF_BANCO_GUAYAQUIL, REF_BANCO_BOLIVARIANO,
                REF_BANCO_PACIFICO, REF_BANCO_INTERNACIONAL, REF_SECURITY_DATA, REF_JEP,
                REF_BANCO_AUSTRO, REF_COOP_AZUAYO, REF_SECURITY_DATA
        };
        BanksNameEnum[] compValues = {
                COMP_BANCO_PICHINCHA, COMP_PRODUBANCO, COMP_BANCO_GUAYAQUIL, COMP_BANCO_BOLIVARIANO,
                COMP_BANCO_PACIFICO, COMP_BANCO_INTERNACIONAL, COMP_SECURITY_DATA, COMP_JEP,
                COMP_BANCO_AUSTRO, COMP_COOP_AZUAYO, COMP_SECURITY_DATA_2
        };

        for (int i = 0; i < refValues.length; i++) {
            SET_NAME_STANDARD.put(refValues[i].getValue(), compValues[i].getValue());
        }

        return SET_NAME_STANDARD;
    }

}
