package ssii.pai2;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class DataDTO {

  @NotNull
  public String fichero;

  @NotNull
  public String token;

  @Min(0)
  @Max(1)
  public RetoEnum reto;

  public String getFichero() {
    return fichero;
  }

  public void setFichero(String fichero) {
    this.fichero = fichero;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public RetoEnum getReto() {
    return reto;
  }

  public void setReto(RetoEnum reto) {
    this.reto = reto;
  }

  
}
