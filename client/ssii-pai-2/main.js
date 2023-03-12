// Estas variable nos va a permitir almacenar todos los nonces de las transacciones activas
// en la caché (localStorage) del navegador
let clientNonces = [];
let serverNonces = [];
let serverNonce = '';
let hash = '';
let nonce = '';
const SERVER_URL = 'http://localhost:8080/'
const headers_ = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Access-Control-Allow-Origin': 'http://localhost:5173/',
  'mode': 'no-cors',
  'Access-Control-Allow-Credentials': 'true',
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

// Esta función permite generar el nonce del cliente, mandarsela al servidor y recibir el nonce del servidor
function sendClientNonce() {

  const requestOptions = {
    method: 'POST',

    headers: headers_,
    body: ''
  }

  while (nonce === '' || clientNonces.includes(nonce)) {
    this.nonce = generarNonce();
  }



  clientNonces.push(nonce);
  localStorage.setItem('clientNonces', JSON.stringify(clientNonces));

  fetch('http://localhost:8080/requestNonce', requestOptions)
    .then((res) => res.text())
    .then((serverNonce) => {
      this.serverNonce = serverNonce;
      serverNonces.push(serverNonce);
      localStorage.setItem('serverNonces', JSON.stringify(serverNonces));
      console.log(serverNonce)
      return serverNonce;
    });
}

// Esta función permitirá enviar al servidor la información de la transacción así como hacer un input validation
async function sendTransaction() {
  // En este objeto se recoge la información del formulario
  const transaction = {
    cuentaOrigen: document.querySelector(".input-search").value,
    cuentaDestino: document.querySelector(".input-token").value,
    cantidad: document.querySelector(".input-challenge").value,
  }

  const finalTransaction = {
    cuentaOrigen: document.querySelector(".input-search").value,
    cuentaDestino: document.querySelector(".input-token").value,
    cantidad: document.querySelector(".input-challenge").value,
    clientHMAC: generateHMAC(transaction, this.nonce),
  }

  console.log(finalTransaction)

  const requestOptions = {
    method: 'POST',

    headers: headers_,
    body: JSON.stringify(finalTransaction)
  }

  fetch(`http://localhost:8080/requestMessage`, requestOptions)
    .then((res) => res.text())
    .then((data) => {
      console.log(requestOptions.body)
      console.log(data)
      alert(`${data}`)
    })
}

// Esta función permite generar el HMAC pasando como parámetro el objeto con la información de la transacción y el
// nonce que utiliza el servidor
function generateHMAC(obj, str) {
  // Convertir el objeto a JSON y codificarlo en base64
  const objB64 = btoa(JSON.stringify(obj));

  // Concatenar el resultado con el string dado
  const concatenated = objB64 + str;

  // Generar el hash del resultado concatenado usando SHA-256
  return concatenated;
}

