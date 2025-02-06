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

function splitQuestionToLetters() {
    const questionElement = document.getElementById('pageform:ot_question');
    if (!questionElement) {
        console.error('Element with id "ot_question" not found.');
        return;
    }

    const text = questionElement.textContent.trim();
    questionElement.textContent = ''; // Töröljük az eredeti tartalmat

    // Szavakra bontás
    const words = text.split(' ');

    words.forEach((word, wordIndex) => {
        // Szóhoz tartozó span létrehozása
        const wordSpan = document.createElement('span');
        wordSpan.classList.add('question-word'); // Szóhoz tartozó osztály hozzáadása

        // Betűk hozzáadása a szóhoz
        for (const letter of word) {
            const letterSpan = document.createElement('span');
            letterSpan.classList.add('question-letter'); // Betűhöz tartozó osztály hozzáadása
            letterSpan.textContent = letter;
            wordSpan.appendChild(letterSpan);
        }

        questionElement.appendChild(wordSpan);

        // Ha nem az utolsó szó, szóközt is hozzáadunk
        if (wordIndex < words.length - 1) {
            questionElement.appendChild(document.createTextNode(' '));
        }
    });
    
    animateLetters();
}

function animateLetters() {

  anime.timeline({loop: false})
    .add({
      targets: '.question-letter',
      rotateY: [-90, 0],
      duration: 7,
      delay: (el, i) => 25 * i,
      begin: function(anim) {
        // Az animáció kezdésekor minden betűt láthatóvá teszünk
        document.querySelectorAll('.question-letter').forEach(el => {
          el.style.visibility = 'visible';
        });
      },
      complete: function() {
        // Az animáció végén az explanation elemet láthatóvá tesszük
        explanationElement = document.querySelector('.explanation');
        if (explanationElement) {
          explanationElement.style.visibility = 'visible';
        }
        explanationElement = document.querySelector('.explanationmedium');
        if (explanationElement) {
          explanationElement.style.visibility = 'visible';
        }
        explanationElement = document.querySelector('.explanationsmall');
        if (explanationElement) {
          explanationElement.style.visibility = 'visible';
        }
      }      
      
      
    })
    ;
    
}   

function animateLettersDisappear() {
  
  explanationElement = document.querySelector('.explanation');
  if (explanationElement) {
    explanationElement.style.visibility = 'hidden';
  }
  explanationElement = document.querySelector('.explanationmedium');
  if (explanationElement) {
    explanationElement.style.visibility = 'hidden';
  }
  explanationElement = document.querySelector('.explanationsmall');
  if (explanationElement) {
    explanationElement.style.visibility = 'hidden';
  }

  anime.timeline({loop: false})
    .add({
      targets: '.question-letter',
      opacity: [1, 0],  // Az elem fokozatosan eltűnik
      scale: [1, 0.5],  // Az elem összemegy, így jobban látható, hogy eltűnik
      duration: 500,  // Az animáció időtartama
      delay: (el, i) => 15 * i  // Késleltetés, hogy a betűk egymás után tűnjenek el
    });
    
}

