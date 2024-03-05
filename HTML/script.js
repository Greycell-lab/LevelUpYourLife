var userCredentials;
var storedCredentials;
var taskbox;
async function getData(){
    const name = document.getElementById('user').value
    const password = document.getElementById('password').value
    const response = await fetch('http://localhost:8080/getLevelData', {
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: `{"user_name": "${name}", "password": "${password}"}`
    });
    const content = await response.json();
    const element = document.getElementById('data');
    userCredentials = {username: name, password: password};
    sessionStorage.setItem('userCredentials', JSON.stringify(userCredentials));
    storedCredentials = sessionStorage.getItem('userCredentials');
    element.innerHTML = `
        <div>
        <p>Name: ${content.name}</p>
        <p>Level: ${content.level}</p>
        <p>EXP: ${content.exp}</p>
        <img src=${content.pet}></img>
        </div>
        <div>
        <label style="display: inline-block;" for="taskbox">Arbeit</label>
        <select id="taskbox"></select>
        <button onclick="sendTask()">Aufgabe wählen</button>
        </div>`;
    taskbox = document.getElementById('taskbox');
    document.getElementById('login').remove();
    await fetchDataAndPopulateDropdown();
}
function populateDropdownList(list, dropdownId) {
    const dropdown = document.getElementById(dropdownId);

    list.forEach((item, index) => {
        const option = document.createElement('option');
        option.value = index; 
        if(item === "COOKING") item = "Kochen";
        else if(item === "VACUUM") item = "Staubsaugen"
        else if(item === "CLEANUP") item = "Aufräumen"
        else if(item === "WASHUP") item = "Abwaschen"
        option.text = item 
        dropdown.add(option);
    });
}
async function fetchDataAndPopulateDropdown() {
    await fetch('http://localhost:8080/getTasks')
        .then(response => response.text())
        .then(data => {
            const cleanedData = data.replace(/[\[\]"]/g, '');
            const list = cleanedData.split(',');
            populateDropdownList(list, 'taskbox');
        })
        .catch(error => console.error('Fehler beim Abrufen der Daten:', error));
}
function checkElement(item){
    if(item === "COOKING"){
        item = "Kochen";
    }
}
async function sendTask(){
    var username = "";
    var password = "";
    var item = taskbox.options[taskbox.selectedIndex].text;
    if(item === "Kochen") item = "COOKING";
        else if(item === "Staubsaugen") item = "VACUUM"
        else if(item === "Aufräumen") item = "CLEANUP"
        else if(item === "Abwaschen") item = "WASHUP"
    if(storedCredentials){
        const parsedCredentials = JSON.parse(storedCredentials);
        username = parsedCredentials.username;
        password = parsedCredentials.password;
    }
    const response = await fetch('http://localhost:8080/startTask', {
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: `{"user": {"user_name": "${username}", "password": "${password}"}, "task": "${item}" }`
    });
}
function setSessionCookie() {
    document.cookie = "benutzerToken=levelupapilogincookie; path=/";
}
function getSessionCookie() {
    var cookies = document.cookie.split(';');
    for (var i = 0; i < cookies.length; i++) {
        var cookie = cookies[i].trim();
        if (cookie.startsWith("benutzerToken=")) {
            return cookie.substring("benutzerToken=".length, cookie.length);
        }
    }
    return null;
}

