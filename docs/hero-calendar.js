/* The hero's calendar card shows the current month with today marked, laid out the way the app draws
   it: a Gregorian month in six rows of seven days, weeks starting on Sunday. "Today" is the date in
   Tehran, where the page shows the time. The static month in the HTML stays as the fallback when this
   does not run. The browser's own Intl does the date work, so there is no date maths to get wrong. */
(function () {
  'use strict';
  var card = document.querySelector('.calendar-card');
  if (!card || !window.Intl) return;

  var iso = new Intl.DateTimeFormat('en-CA', {
    timeZone: 'Asia/Tehran', year: 'numeric', month: '2-digit', day: '2-digit'
  }).format(new Date());
  var m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(iso);
  if (!m) return;
  var year = +m[1], month = +m[2] - 1, today = +m[3];

  /* Six weeks, as in the app: the days before the 1st and after the last day are shown dimmed. */
  var lead = new Date(Date.UTC(year, month, 1)).getUTCDay();
  var cells = [];
  for (var i = 0; i < 42; i++) {
    var day = new Date(Date.UTC(year, month, 1 - lead + i));
    var cls = day.getUTCMonth() !== month ? 'muted' : (day.getUTCDate() === today ? 'today' : '');
    cells.push({ d: day.getUTCDate(), cls: cls });
  }

  var grid = card.querySelector('.grid');
  var label = card.querySelector('.month span');
  if (!grid || !label) return;
  var head = [].slice.call(grid.children, 0, 7);
  grid.replaceChildren.apply(grid, head.concat(cells.map(function (c) {
    var s = document.createElement('span');
    if (c.cls) s.className = c.cls;
    if (c.cls === 'today') s.setAttribute('aria-current', 'date');
    s.textContent = c.d;
    return s;
  })));
  label.textContent = new Intl.DateTimeFormat(document.documentElement.lang || 'en', {
    timeZone: 'UTC', year: 'numeric', month: 'long'
  }).format(new Date(Date.UTC(year, month, 1)));
})();
