"use strict";

/* Replace the UTC timestamps of articles and comments with new ones that are offset to the
 * browser's local time.
 *
 * Timestamps are selected by the 'time-created' and 'time-updated' CSS classes. Their
 * data-time-created and data-time-updated attributes carry timestamps in ms.
 */

var today = new Date();
var offset = -(today.getTimezoneOffset() * 60000); // getTimezoneOffset from minutes to ms

function pad(n){
    return n<10 ? '0'+n : n;
}

function timeString(d) {
    return pad(d.getUTCHours())+':'
	+ pad(d.getUTCMinutes());
}

function ISODateString(d) {
    return d.getUTCFullYear()+'-'
	+ pad(d.getUTCMonth()+1)+'-'
	+ pad(d.getUTCDate());
}

function fullISODateString(d) {
    return d.getUTCFullYear()+'-'
	+ pad(d.getUTCMonth()+1)+'-'
	+ pad(d.getUTCDate())+'T'
	+ pad(d.getUTCHours())+':'
	+ pad(d.getUTCMinutes())+':'
	+ pad(d.getUTCSeconds())+'Z';
}

function updateTimesColl(collection, dataAttribute){
    for (var i=0, n; n = collection[i++];) {
	var atUTC = parseInt(n.getAttribute(dataAttribute));
	var offseted = atUTC + offset;
	var upDate = new Date(offseted);
	n.setAttribute('datetime', fullISODateString(upDate));
	n.innerHTML = ISODateString(upDate)
	    + ' <span class="hour-minute">'
	    + timeString(upDate)
	    + '</span></time></p></div>';
    };
}

function updateTimes(){
    var createdTimes = document.getElementsByClassName('time-created');
    var updatedTimes = document.getElementsByClassName('time-updated');

    updateTimesColl(createdTimes, 'data-time-created');
    updateTimesColl(updatedTimes, 'data-time-updated');
}

window.addEventListener('load', function(){updateTimes();}, false);