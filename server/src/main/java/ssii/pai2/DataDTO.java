package ssii.pai2;
import javax.validation.constraints.Size;

public class DataDTO {

  @Size(min = 20, max = 40)
  public String clientHMAC;

  public String messageBase64;


  public DataDTO(String clientHMAC, String messageBase64) {
    this.clientHMAC = clientHMAC;
    this.messageBase64 = messageBase64;
  }

  public DataDTO() {}

  public String getClientHMAC() {
    return clientHMAC;
  }

  public void setClientHMAC(String clientHMAC) {
    this.clientHMAC = clientHMAC;
  }

  public String getMessageBase64() {
    return messageBase64;
  }

  public void setMessageBase64(String messageBase64) {
    this.messageBase64 = messageBase64;
  }
}