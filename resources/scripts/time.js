"use strict";

/* Replace the UTC timestamps of articles and comments with ones offset to the browser's local time.
 *
 * Defined before loading this file:
 * serverNow: the current server time in UTC, ms
 *
 * Timestamps have 'time-created' and 'time-updated' CSS classes and timestamps in ms as #ids
 */

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

function updateTimes(collection){
    for (var i=0, n; n = collection[i++];) {
	var atUTC = parseInt(n.getAttribute('id'));
	var offseted = atUTC + offset;
	var upDate = new Date(offseted);
	n.setAttribute('datetime', fullISODateString(upDate));
	n.innerHTML = ISODateString(upDate)
	    + ' <span class="hour-minute">'
	    + timeString(upDate)
	    + '</span></time></p></div>';
    };
}

var today = new Date();
var offset = -(today.getTimezoneOffset() * 60000); // getTimezoneOffset from minutes to ms

var createdTimes = document.getElementsByClassName('time-created');
var updatedTimes = document.getElementsByClassName('time-updated');

updateTimes(createdTimes);
updateTimes(updatedTimes);