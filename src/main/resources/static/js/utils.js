function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

const formatFromUnixTimeToDate = (unixTime) => {
    return new Date(unixTime).toLocaleDateString();
}

const formatFromUnixTimeToHoursMinutes = (unixTime) => {
    let date = new Date(unixTime);
    let timeString = date.toLocaleTimeString();
    let [hours, minutes] = timeString.split(':').slice(0, 2);
    return `${hours.padStart(2, '0')}:${minutes}`;
}

const getValueForInputDate = (unix) => {
    return new Date(unix).toLocaleDateString('en-CA');
}

function debounce(func, timeout = 300) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => {
            func.apply(this, args);
        }, timeout);
    };
}

const getUrlParameters = (apiURL, params) => {
    let url = apiURL + '?';
    for (let [key, value] of Object.entries(params)) {
        if (value) {
            url += `${key}=${value}&`;
        }
    }
    url = url.slice(0, -1);
    return url;
}

function handleAddEvent(dom,eventType,func){
    dom.on(eventType,debounce(func,500));
}

const callToolTip = () => {
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
    const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))
}

function formatDateTime(date) {
    const d = new Date(Number(date));
    const day = String(d.getDate()).padStart(2, "0");
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const year = d.getFullYear();
    const hours = String(d.getHours()).padStart(2, "0");
    const minutes = String(d.getMinutes()).padStart(2, "0");
    return `${day}/${month}/${year} ${hours}:${minutes}`;
}