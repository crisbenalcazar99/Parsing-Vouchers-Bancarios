package net.security.data.microservicesocr.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.security.data.microservicesocr.enums.BanksAccountsEnum;
import net.security.data.microservicesocr.enums.FieldsVouchersEnum;
import java.util.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "datos_voucher", schema = "public", catalog = "db_microservicesOCR")
public class VouchersEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVoucher;

    @Basic
    @Column(name = "estado", nullable = false, columnDefinition = "boolean default true")
    private boolean stateRegister;

    @Basic
    @Column(name = "id_transaccion", nullable = false)
    private Long idTransaction;

    @Basic
    @Column(name = "referencia_transaccion")
    private Long transferReference;

    @Basic
    @Column(name = "valor_transferido", nullable = false)
    private Double transferredValue;

    @Basic
    @Column(name = "valor_debitado")
    private Double debitedValue;

    @Basic
    @Column(name = "valor_comision")
    private Double commissionValue;

    @Basic
    @Column(name = "fecha_transferencia", nullable = false)
    private LocalDate transferDate;

    @Column(name = "fecha_registro")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordDate;

    @Basic
    @Column(name = "nombre_banco_destino")
    private String destinationBankName;

    @Basic
    @Column(name = "cuenta_banco_destino")
    private String destinationBankAccount;

    @Basic
    @Column(name = "titular_banco_destino")
    private String destinationAccountHolderName;

    @Basic
    @Column(name = "nombre_banco_origen")
    private String origenBankName;

    @Basic
    @Column(name = "cuenta_banco_origen")
    private String origenBankAccount;

    @Basic
    @Column(name = "titular_banco_origen")
    private String origenAccountHolderName;

    @Basic
    @Column(name = "transferencia_verificada", nullable = false, columnDefinition = "boolean default false")
    private boolean verifiedTransfer;

    @Basic
    @Column(name = "ruta_imagen", unique = true)
    private String urlPath;

    @Basic
    @Column(name = "referencia_coincidencia")
    private Long matchReference;

    @Basic
    @ManyToOne
    @JoinColumn(name = "tipo_guardado", referencedColumnName = "codigo_catalogo")
    @JsonBackReference
    private CatalogEntity catalogoEntity;

    public void setTransferDate(String transferDate) {
        this.transferDate = LocalDate.parse(transferDate);
    }

    // Convertir en doble los valores de montos recibidos en formato String
    public void setCommissionValue(String commissionValue) {
        this.commissionValue = Double.valueOf(commissionValue.replaceAll("[^0-9.,]", ""));
    }

    public void setDebitedValue(String debitedValue) {
        this.debitedValue = Double.valueOf(debitedValue.replaceAll("[^0-9.,]", ""));
    }

    public void setTransferredValue(String transferredValue) {
        this.transferredValue = Double.valueOf(transferredValue.replaceAll("[^0-9.,]", ""));
    }

    // Convertir a Long el valor recibido en string
    public void setTransferReference(String transferReference) {
        this.transferReference = Long.valueOf(transferReference.replaceAll("[^0-9]", ""));
    }

    // Normaliza el Numero de cuenta bancaria de Security Data
    public void setDestinationBankAccount(String bankNumber) {
        bankNumber = bankNumber.replaceAll("[^0-9]", "");
        String regexExp = ".*" + bankNumber.substring(Math.max(0, bankNumber.length() - 2)) + "?$";
        Pattern pattern = Pattern.compile(regexExp);
        HashMap<String, String> BANK_NUMBERS_ACCOUNT = BanksAccountsEnum.toMap();
        for (String value : BANK_NUMBERS_ACCOUNT.values()) {
            if (pattern.matcher(value).find()) {
                bankNumber = value;
                break;
            }
        }
        this.destinationBankAccount = bankNumber;
    }

    public VouchersEntity(Long idTransaction, Boolean stateRegister, HashMap<String, String> dictEntities,
            String urlPath, CatalogEntity catalogTypeSave) {
        this.setIdTransaction(idTransaction);
        this.setStateRegister(stateRegister);
        this.setCatalogoEntity(catalogTypeSave);
        this.setUrlPath(urlPath);
        Map<String, Consumer<String>> actionMap = new HashMap<>();
        actionMap.put(FieldsVouchersEnum.TRANSFERRED_VALUE.getValue(), this::setTransferredValue);
        actionMap.put(FieldsVouchersEnum.DEBITED_VALUE.getValue(), this::setDebitedValue);
        actionMap.put(FieldsVouchersEnum.COMMISSION_VALUE.getValue(), this::setCommissionValue);
        actionMap.put(FieldsVouchersEnum.TRANSFER_REFERENCE.getValue(), this::setTransferReference);
        actionMap.put(FieldsVouchersEnum.TRANSFER_DATE.getValue(), this::setTransferDate);
        actionMap.put(FieldsVouchersEnum.DESTINATION_BANK_NAME.getValue(), this::setDestinationBankName);
        actionMap.put(FieldsVouchersEnum.DESTINATION_BANK_ACCOUNT.getValue(), this::setDestinationBankAccount);
        actionMap.put(FieldsVouchersEnum.DESTINATION_ACCOUNT_HOLDER_NAME.getValue(),
                this::setDestinationAccountHolderName);
        actionMap.put(FieldsVouchersEnum.ORIGEN_BANK_NAME.getValue(), this::setOrigenBankName);
        actionMap.put(FieldsVouchersEnum.ORIGEN_BANK_ACCOUNT.getValue(), this::setOrigenBankAccount);
        actionMap.put(FieldsVouchersEnum.ORIGEN_ACCOUNT_HOLDER_NAME.getValue(), this::setOrigenAccountHolderName);

        dictEntities.forEach((key, value) -> {
            Consumer<String> action = actionMap.get(key);
            if (action != null)
                action.accept(value);
            else
                System.out.println("Accion NUla" + key);
        });
    }

    /*
     * @OneToOne(mappedBy = "vouchersDataByIdVoucherFk")
     * 
     * @JsonBackReference
     * public MatchesFoundEntity getMatchesFoundsByIdVoucher() {
     * return matchesFoundsByIdVoucher;
     * }
     * 
     */

}
