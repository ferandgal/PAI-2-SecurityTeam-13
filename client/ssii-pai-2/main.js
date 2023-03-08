async function getData() {
  const data = {
    fichero: document.querySelector(".input-search").value,
    token: document.querySelector(".input-token").value,
    reto: document.querySelector(".input-challenge").value == 0 ? "RETO1" : "RETO2"
  }
  const headers_ = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'Access-Control-Allow-Origin': 'http://localhost:5173/',
    'mode': 'no-cors',
    'Access-Control-Allow-Credentials': 'true',
  }

  const requestOptions = {
    method: 'POST',

    headers: headers_,
    body: JSON.stringify(data)
  }

  fetch('http://localhost:8080/data', requestOptions)
    .then((res) => res.text())
    .then((data) => {
      console.log(requestOptions.body)
      console.log(data)
      document.querySelector(".code").innerHTML = "Código Hash: " + data;
    })
}

var countries = [];
var inputElem = null;
var resultsElem = null;
var activeIndex = 0;
var filteredResults = [];

function init(body) {

  const headers_ = {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': 'http://localhost:5173/',
    'mode': 'cors',
    'Access-Control-Allow-Credentials': 'true',
  }

  const requestOptions = {
    method: 'GET',
    headers: headers_,
    body: (JSON.stringify(body))
  }

  fetch('http://localhost:8080/data/all', requestOptions)
    .then((response) => response.json())
    .then((data) => {
      for(var key in data) {
        var option = document.createElement("option");
        option.text = data[key];
        countries.push(option.innerText);
      }
    });

  resultsElem = document.querySelector("ul");
  inputElem = document.querySelector("input");

  resultsElem.addEventListener("click", (event) => {
    handleResultClick(event);
  });
  inputElem.addEventListener("input", (event) => {
    autocomplete(event);
  });
  inputElem.addEventListener("keyup", (event) => {
    handleResultKeyDown(event);
  });
}

function autocomplete() {
  const value = inputElem.value;
  if (!value) {
    hideResults();
    inputElem.value = "";
    return;
  }
  filteredResults = countries.filter((country) => {
    return country.toLowerCase().startsWith(value.toLowerCase());
  });

  resultsElem.innerHTML = filteredResults
    .map((result, index) => {
      const isSelected = index === 0;
      return `
        <li
          id='autocomplete-result-${index}'
          class='autocomplete-result${isSelected ? " selected" : ""}'
          role='option'
          ${isSelected ? "aria-selected='true'" : ""}
        >
          ${result}
        </li>
      `;
    })
    .join("");
  resultsElem.classList.remove("hidden");
}

function handleResultClick() {
  if (event.target && event.target.nodeName === "LI") {
    selectItem(event.target);
  }
}

function handleResultKeyDown(event) {
  const { key } = event;
  const activeItem = this.getItemAt(activeIndex);
  if (activeItem) {
   activeItem.classList.remove('selected');
   activeItem.setAttribute('aria-selected', 'false');
  }
  switch (key) {
    case "Backspace":
      return;
    case "Escape":
      hideResults();
      inputElem.value = "";
      return;
    case "ArrowUp": {
      if (activeIndex === 0) {
        activeIndex = filteredResults.length - 1;
      }
      activeIndex--;
      break;
    }
    case "ArrowDown": {
      if (activeIndex === filteredResults.length - 1) {
        activeIndex = 0;
      }
      activeIndex++;
      break;
    }
    default:
      selectFirstResult();
  }
  console.log(activeIndex);
  selectResult();
}
function selectFirstResult() {
  activeIndex = 0;
}

function selectResult() {
  const value = inputElem.value;
  const autocompleteValue = filteredResults[activeIndex];
  const activeItem = this.getItemAt(activeIndex);
  if (activeItem) {
   activeItem.classList.add('selected');
   activeItem.setAttribute('aria-selected', 'true');
  }
  if (!value || !autocompleteValue) {
    return;
  }
  if (value !== autocompleteValue) {
    inputElem.value = autocompleteValue;
    inputElem.setSelectionRange(value.length, autocompleteValue.length);
  }
}
function selectItem(node) {
  if (node) {
    console.log(node);
    inputElem.value = node.innerText;
    hideResults();
  }
}

function hideResults() {
  this.resultsElem.innerHTML = "";
  this.resultsElem.classList.add("hidden");
}

function getItemAt(index) {
  return this.resultsElem.querySelector(`#autocomplete-result-${index}`)
}

init();
