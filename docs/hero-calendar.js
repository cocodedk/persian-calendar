/* The hero's calendar card shows the real Jalali month in Tehran, with today marked.
   The static month in the HTML stays as the fallback when this does not run. The browser's own
   Persian calendar (Intl, ca-persian) does the conversion, so there is no date maths to get wrong. */
(function () {
  'use strict';
  var card = document.querySelector('.calendar-card');
  if (!card || !window.Intl) return;

  var DAY = 864e5;
  var fmt = new Intl.DateTimeFormat('en-US-u-ca-persian', {
    timeZone: 'Asia/Tehran', year: 'numeric', month: 'long', day: 'numeric', weekday: 'short'
  });
  function parts(t) {
    var p = {};
    fmt.formatToParts(new Date(t)).forEach(function (x) { p[x.type] = x.value; });
    return { y: parseInt(p.year, 10), m: p.month, d: parseInt(p.day, 10), wd: p.weekday };
  }

  var now = Date.now();
  var today = parts(now);
  if (!today.y || !today.d) return;
  var first = now - (today.d - 1) * DAY;
  /* The Iranian week starts on Saturday; Friday is the weekend. */
  var ORDER = ['Sat', 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri'];
  var lead = ORDER.indexOf(parts(first).wd);
  if (lead < 0) return;

  var cells = [];
  for (var i = lead; i > 0; i--) cells.push({ d: parts(first - i * DAY).d, cls: 'muted' });
  for (var t = first; parts(t).m === today.m; t += DAY) {
    var p = parts(t);
    cells.push({ d: p.d, cls: p.d === today.d ? 'today' : (p.wd === 'Fri' ? 'weekend' : '') });
  }
  for (var n = 1; cells.length % 7; n++) cells.push({ d: n, cls: 'muted' });

  var grid = card.querySelector('.grid');
  var label = card.querySelector('.month span');
  var head = [].slice.call(grid.children, 0, 7);
  grid.replaceChildren.apply(grid, head.concat(cells.map(function (c) {
    var s = document.createElement('span');
    if (c.cls) s.className = c.cls;
    if (c.cls === 'today') s.setAttribute('aria-current', 'date');
    s.textContent = c.d;
    return s;
  })));
  label.textContent = today.m + ' ' + today.y;
})();
