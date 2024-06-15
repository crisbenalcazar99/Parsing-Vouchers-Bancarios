package net.security.data.microservicesocr.enums;

public enum FieldsVouchersEnum {
    TRANSFERRED_VALUE("Valor_Transferido"),
    DEBITED_VALUE("Valor_Debitado"),
    COMMISSION_VALUE("Valor_Comision"),
    TRANSFER_REFERENCE("Referencia_Transaccion"),
    TRANSFER_DATE("Fecha_Transferencia"),
    DESTINATION_BANK_NAME("Nombre_Banco_Destino"),
    DESTINATION_BANK_ACCOUNT("Numero_Cuenta_Destino"),
    DESTINATION_ACCOUNT_HOLDER_NAME("Nombre_Titular_Destino"),
    ORIGEN_BANK_NAME("Nombre_Banco_Origen"),
    ORIGEN_BANK_ACCOUNT("Numero_Cuenta_Origen"),
    ORIGEN_ACCOUNT_HOLDER_NAME("Nombre_Titular_Origen");

    private final String value;
    FieldsVouchersEnum(String value){
        this.value = value;
    }
    public String getValue(){
        return value;
    }
}
