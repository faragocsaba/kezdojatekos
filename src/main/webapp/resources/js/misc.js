function getfilename() {
  var if_loadgame = document.getElementById("pageform:if_loadfile");
  if (if_loadgame !== null) {
    var in_filename = document.getElementById("in_filename");
    if (in_filename !== null) {
      if_loadgame.onchange = function () {
        if (this.files[0]) {
          in_filename.value = this.files[0].name;
        } else {
          in_filename.value = "";
        }
      };
    }
  }
}

function addOutsideClickListener() {
    $(document).on('click.outsideDialog', function(event) {
        const dialog = $('.ui-dialog');
        if (!dialog.is(event.target) && dialog.has(event.target).length === 0) {
            PF('dlg_about').hide();
            $(document).off('click.outsideDialog');
        }
    });
}

function removeOutsideClickListener() {
    $(document).off('click.outsideDialog');
}