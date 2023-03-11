// Estas variable nos va a permitir almacenar todos los nonces de las transacciones activas
// en la caché (localStorage) del navegador
let clientNonces = [];
let serverNonces = [];
let serverNonce = '';
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
  let nonce = ''
  while (nonce === '' || clientNonces.includes(nonce)) {
    nonce = generarNonce();
    console.log(nonce)
  }

  clientNonces.push(nonce);
  localStorage.setItem('clientNonces', JSON.stringify(clientNonces));

  const requestOptions = {
    method: 'POST',

    headers: headers_,
    body: nonce
  }

  fetch(`${SERVER_URL}requestNonce`, requestOptions)
    .then((res) => res.text())
    .then((serverNonce) => {
      this.serverNonce = serverNonce;
      serverNonces.push(serverNonce);
      localStorage.setItem('serverNonces', JSON.stringify(serverNonces));
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

  const requestOptions = {
    method: 'POST',

    headers: headers_,
    body: JSON.stringify(transaction)
  }

  fetch(`${data} + `, requestOptions)
    .then((res) => res.text())
    .then((data) => {
      console.log(requestOptions.body)
      console.log(data)
      alert(`La transacción ${data} se ha realizado con éxito`)
    })
}

async function hashSHA256(mensaje) {
  // Convertimos el mensaje en un array de bytes (Uint8Array)
  const mensajeBytes = new TextEncoder().encode(mensaje);

  // Obtenemos un objeto CryptoKey a partir de la clave "SHA-256"
  const clave = await crypto.subtle.importKey(
    "raw",
    new Uint8Array(32),
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"]
  );

  // Generamos el hash del mensaje utilizando la clave
  const hash = await crypto.subtle.sign(
    "HMAC",
    clave,
    mensajeBytes
  );

  // Devolvemos el hash en formato hexadecimal
  return Array.from(new Uint8Array(hash))
    .map(b => b.toString(16).padStart(2, "0"))
    .join("");
}


// Esta función permite generar el HMAC pasando como parámetro el objeto con la información de la transacción y el
// nonce que utiliza el servidor
function generarHMAC(objeto, serverNonce) {
  const json = JSON.stringify(objeto);
  const base64 = btoa(json);
  const cadenaBase64 = `${base64}${serverNonce}`;
  const hash = hashSHA256(cadenaBase64);
  hash.then(data => console.log(data))
  return hash;
}
