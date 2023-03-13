// Estas variable nos va a permitir almacenar todos los nonces de las transacciones activas
// en la caché (localStorage) del navegador
class transferDataObject {
  constructor (nonceCliente, nonceServidor, transferencia, hmac) {
    this.nonceCliente = nonceCliente;
    this.nonceServidor = nonceServidor;
    this.transferencia = transferencia;
    this.hmac = hmac;
  }
}

const SERVER_URL = 'http://localhost:8080/'
async function requestAPI(endpoint, method, body) {
  const headers_ = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'Access-Control-Allow-Origin': 'http://localhost:5173/',
    'mode': 'no-cors',
    'Access-Control-Allow-Credentials': 'true',
  }

  const requestOptions = {
    method: method,
    headers: headers_,
    body: body
  }

  const response = await fetch(SERVER_URL + endpoint, requestOptions);
  return response;
}


// Esta función permite generar un nonce aleatorio para cada transación
function generarNonce() {
  const array = new Uint8Array(32);
  window.crypto.getRandomValues(array);
  let nonce = '';
  for (let i = 0; i < array.length; i++) {
    nonce += array[i].toString(16).padStart(2, '0');
  }
  return btoa(nonce);
}

function makeBankTransaction() {
  event.preventDefault();
  const transaction = {
    cuentaOrigen: document.querySelector(".input-search").value,
    cuentaDestino: document.querySelector(".input-token").value,
    cantidad: document.querySelector(".input-challenge").value,
  }

  const finalTransaction = {
    messageBase64: '',
    clientHMAC: '',
  }

  const nonceCliente = generarNonce();
  console.log(nonceCliente);
  requestAPI('requestNonce', 'POST', nonceCliente)
    .then(serverNonce => serverNonce.text())
    .then(serverNonce => {
      console.log('Server Nonce: ' + serverNonce)
      generateHMAC(transaction, serverNonce)
        .then(hmac => {
          console.log('Código HMAC: ' + hmac);
          finalTransaction.messageBase64 = btoa(JSON.stringify(transaction));
          finalTransaction.clientHMAC = hmac;
          console.log(finalTransaction);
          requestAPI('requestMessage', 'POST', finalTransaction)
            .then(response => console.log(response));
        });
    });
}

// Esta función permite generar el HMAC pasando como parámetro el objeto con la información de la transacción y el
// nonce que utiliza el servidor
function generateHMAC(obj, str) {
  // Convertir el objeto a JSON y codificarlo en base64
  const objB64 = btoa(JSON.stringify(obj));

  // Concatenar el resultado con el string dado
  const concatenated = objB64 + str;
  // Generar el hash del resultado concatenado usando SHA-256
  return sha256(concatenated)
}

async function sha256(str) {

  const secretKey = 'my-secret-key';
  const hmac = CryptoJS.HmacSHA256(str, secretKey);

  return btoa(hmac)
}



