package ssii.pai2;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class DataDTO {

  @NotNull
  public String cuentaOrigen;

  @NotNull
  public String cuentaDestino;

  @Min(20)
  public Integer cantidad;

  public DataDTO(String cuentaOrigen, String cuentaDestino, Integer cantidad) {
    this.cuentaOrigen = cuentaOrigen;
    this.cuentaDestino = cuentaDestino;
    this.cantidad = cantidad;
  }

  public String getCuentaOrigen() {
    return cuentaOrigen;
  }

  public void setCuentaOrigen(String cuentaOrigen) {
    this.cuentaOrigen = cuentaOrigen;
  }

  public String getCuentaDestino() {
    return cuentaDestino;
  }

  public void setCuentaDestino(String cuentaDestino) {
    this.cuentaDestino = cuentaDestino;
  }

  public Integer getCantidad() {
    return cantidad;
  }

  public void setCantidad(Integer cantidad) {
    this.cantidad = cantidad;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cuentaOrigen == null) ? 0 : cuentaOrigen.hashCode());
    result = prime * result + ((cuentaDestino == null) ? 0 : cuentaDestino.hashCode());
    result = prime * result + ((cantidad == null) ? 0 : cantidad.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DataDTO other = (DataDTO) obj;
    if (cuentaOrigen == null) {
      if (other.cuentaOrigen != null)
        return false;
    } else if (!cuentaOrigen.equals(other.cuentaOrigen))
      return false;
    if (cuentaDestino == null) {
      if (other.cuentaDestino != null)
        return false;
    } else if (!cuentaDestino.equals(other.cuentaDestino))
      return false;
    if (cantidad == null) {
      if (other.cantidad != null)
        return false;
    } else if (!cantidad.equals(other.cantidad))
      return false;
    return true;
  }
}
