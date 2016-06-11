var logboxActive = false;
var dark = true;

function log(text) {
  var old = document.getElementById('log').innerHTML;
  document.getElementById('log').innerHTML = old + new Date().toTimeString() + ': ' + text + '\n';
}

function nextImage() {
  showImage('sunny.gif');
  AppCtrl.next();
}

function prevImage() {
  showImage('sunny.gif');
  AppCtrl.prev();
}

function loadPage() {
  showImage('sunny.gif');
  var number = parseInt(document.getElementById('page').value, 10);
  if (isNaN(number)) {
    showError('Invalid page');
    return;
  }
  AppCtrl.loadPage(number);
}

function toggleLogbox() {
  if (logboxActive) {
    document.getElementById('logBox').style.display = 'none';
  } else {
    document.getElementById('logBox').style.display = 'block';
  }
  logboxActive = !logboxActive;
}

function toggleBgColor() {
  if (dark) {
    document.getElementsByTagName('body')[0].className = '';
  } else {
    document.getElementsByTagName('body')[0].className = 'dark';
  }
  dark = !dark;
}

function showError(type) {
  if (!logboxActive) {
    toggleLogbox();
  }
  log('error ' + type);
}

function showImage(url) {
  log('image ' + url);
  document.getElementById('image').src = url;
}

function updatePageNumber(number) {
  document.getElementById('page').value = number;
}

window.onload = function () {
  var version = AppCtrl.getVersion();
  log('loaded ' + version);
  document.getElementById('version').innerHTML = version;
  showImage('sunny.gif');
  AppCtrl.domReady();
};