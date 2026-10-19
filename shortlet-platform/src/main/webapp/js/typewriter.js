document.addEventListener("DOMContentLoaded", function() {
    const phrases = [
        "Short stays, serious comfort",
        "Find your perfect shortlet",
        "Book in seconds, relax in style",
        "Shortlet – your home away from home"
    ];
    
    let phraseIndex = 0;
    let charIndex = 0;
    let isDeleting = false;
    const typedTextSpan = document.getElementById("animated-text");
    const typingSpeed = 70;      // ms per character
    const pauseBetween = 2200;   // pause after full phrase (2.2s)
    
    function type() {
        if (!typedTextSpan) return;
        const currentPhrase = phrases[phraseIndex];
        
        if (!isDeleting) {
            // Typing forward
            typedTextSpan.textContent = currentPhrase.substring(0, charIndex + 1);
            charIndex++;
            
            if (charIndex === currentPhrase.length) {
                // Finished typing the phrase
                isDeleting = true;
                setTimeout(type, pauseBetween);
                return;
            }
        } else {
            // Deleting (erasing characters)
            typedTextSpan.textContent = currentPhrase.substring(0, charIndex - 1);
            charIndex--;
            
            if (charIndex === 0) {
                isDeleting = false;
                phraseIndex = (phraseIndex + 1) % phrases.length;
                setTimeout(type, 150); // Pause briefly before starting the next phrase
                return;
            }
        }
        
        let speed = isDeleting ? typingSpeed / 2.2 : typingSpeed;
        setTimeout(type, speed);
    }
    
    // Start the animation after DOM load settles
    setTimeout(type, 300);
});
