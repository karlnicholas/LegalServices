
function getAdvancedSearchFields(term) {
  var m = ['', '', '', ''];
  
  if ( term === null || term === '' ) return m;
  var terms = term.split(' ');
  var all = '';
  var not = '';
  var any = '';
  var exact = '';
  var ex = false;
  var i;
  for(i=0; i < terms.length; ++i) {
    var t = terms[i];
    if ( !ex && t.startsWith('+')) all=all.concat(t.substring(1) + " ");
    else if ( !ex && t.startsWith('-')) not=not.concat(t.substring(1) + ' ' );
    else if ( !ex && (t.startsWith('"') && t.trim().endsWith('"')) ) {
      exact=exact.concat(t.substring(1, t.length-1) + " ");
    }
    else if ( !ex && t.startsWith('"')) {
      exact=exact.concat(t.substring(1) + ' ');
      ex = true;
    }
    else if ( ex && !t.endsWith('"') ) {
      exact=exact.concat(t) + ' ';
    }
    else if ( ex && t.endsWith('"')) {
      exact=exact.concat(t.substring(0, t.length-1)) + ' ';
      ex = false;
    }
    else any = any.concat(t) + ' ';
  }
  m[0] = all.trim();
  m[1] = not.trim();
  m[2] = any.trim();
  m[3] = exact.trim();
  return m;
}

function getSearchTerm(all, not, any, exact) {
  // navbar clear term and fragments
  var term = '';
  if ( 
    !isEmpty(all)
    || !isEmpty(not)
    || !isEmpty(any)
    || !isEmpty(exact)
  ) {
    if ( !isEmpty(all) ) {
      term = term + appendOp(all, '+');
    }
    if ( !isEmpty(not) ) {
      term = term + appendOp(not, '-');
    }
    if ( !isEmpty(any) ) {
      term = term + any + ' ';
    }
    if ( !isEmpty(exact) ) {
      term = term + '"' + exact + '"';
    }
  }
  return term.trim();
}
function isEmpty(value) {
  return (typeof value == 'string' && !value.trim()) || typeof value === 'undefined' || value === null;
}
function appendOp(val, op) {
  val = val.trim();
  if ( isEmpty(val)) return '';
  var terms = val.trim().split(' ');
  var sb = '';
  var i;
  for (i = 0; i < terms.length; ++i) {
          sb = sb + op + terms[i] + ' ';
  }
  return sb;
}
export {getAdvancedSearchFields, getSearchTerm}