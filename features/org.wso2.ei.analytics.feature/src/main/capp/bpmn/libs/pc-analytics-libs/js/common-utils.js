function getUrlVars() {
    var vars = [], hash;
    var hashes = top.location.href.slice(top.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

function getTimeScale(milliseconds) {
    var seconds = milliseconds/(1000);
    var minutes, hours, days;
    var timeScale;

    minutes = seconds/60;
    if(seconds < 60) {
        timeScale = 'sec'
    } else if(seconds/60 < 60) {
        timeScale = 'min';
    } else if(minutes/3600 < 24) {
        timeScale = 'hrs';
    } else {
        days = minutes/(3600*24);
        timeScale = 'days';
    }
    return timeScale;
}

function convertTime(unit, time) {
    switch(unit) {
        case 'sec':
            time = time/1000;
            break;
        case 'min':
            time = time/(1000*60)
            break;
        case 'hrs':
            time = time/(1000*60*60);
            break;
        case 'days':
            time = time/(1000*60*60*24);
            break;
        default:
            break;
    }
    return time;
}