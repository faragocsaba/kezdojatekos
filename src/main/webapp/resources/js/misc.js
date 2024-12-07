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
