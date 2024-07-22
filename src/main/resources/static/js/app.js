document.addEventListener("DOMContentLoaded", function() {
    /**
     * Form Select
     */
    class FormSelect {
        constructor($el) {
            this.$el = $el;
            this.options = [...$el.children];
            this.init();
        }

        init() {
            this.createElements();
            this.addEvents();
            this.$el.parentElement.removeChild(this.$el);
        }

        createElements() {
            // Input for value
            this.valueInput = document.createElement("input");
            this.valueInput.type = "text";
            this.valueInput.name = this.$el.name;

            // Dropdown container
            this.dropdown = document.createElement("div");
            this.dropdown.classList.add("dropdown");

            // List container
            this.ul = document.createElement("ul");

            // All list options
            this.options.forEach((el, i) => {
                const li = document.createElement("li");
                li.dataset.value = el.value;
                li.innerText = el.innerText;

                if (i === 0) {
                    // First clickable option
                    this.current = document.createElement("div");
                    this.current.innerText = el.innerText;
                    this.dropdown.appendChild(this.current);
                    this.valueInput.value = el.value;
                    li.classList.add("selected");
                }

                this.ul.appendChild(li);
            });

            this.dropdown.appendChild(this.ul);
            this.dropdown.appendChild(this.valueInput);
            this.$el.parentElement.appendChild(this.dropdown);
        }

        addEvents() {
            this.dropdown.addEventListener("click", e => {
                const target = e.target;
                this.dropdown.classList.toggle("selecting");

                // Save new value only when clicked on li
                if (target.tagName === "LI") {
                    this.valueInput.value = target.dataset.value;
                    this.current.innerText = target.innerText;
                }
            });
        }
    }

    document.querySelectorAll(".form-group--dropdown select").forEach(el => {
        new FormSelect(el);
    });

    /**
     * Hide elements when clicked on document
     */
    document.addEventListener("click", function(e) {
        const target = e.target;
        const tagName = target.tagName;

        if (target.classList.contains("dropdown")) return false;

        if (tagName === "LI" && target.parentElement.parentElement.classList.contains("dropdown")) {
            return false;
        }

        if (tagName === "DIV" && target.parentElement.classList.contains("dropdown")) {
            return false;
        }

        document.querySelectorAll(".form-group--dropdown .dropdown").forEach(el => {
            el.classList.remove("selecting");
        });
    });

    /**
     * Switching between form steps
     */
    class FormSteps {
        constructor(form) {
            this.$form = form;
            this.$next = form.querySelectorAll(".next-step");
            this.$prev = form.querySelectorAll(".prev-step");
            this.$step = form.querySelector(".form--steps-counter span");
            this.currentStep = 1;

            this.$stepInstructions = form.querySelectorAll(".form--steps-instructions p");
            const $stepForms = form.querySelectorAll("form > div");
            this.slides = [...this.$stepInstructions, ...$stepForms];

            this.init();
        }

        /**
         * Init all methods
         */
        init() {
            this.events();
            this.updateForm();

            // Attach event listeners for summary updates (only once)
            document.querySelectorAll('input[name="categories"], input[name="donation.quantity"]').forEach(input => {
                input.addEventListener('change', this.updateQuantity.bind(this));
            });

            // Attach event listeners for summary updates (only once)
            document.querySelectorAll('input[name="categories"], input[name="donation.quantity"]').forEach(input => {
                input.addEventListener('change', this.updateDonationSummary.bind(this));
            });

            document.querySelectorAll('input[type="radio"][name="donation.institution"]').forEach(input => {
                input.addEventListener('change', this.updateOrganizationSummary.bind(this));
            });

            document.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
                checkbox.addEventListener('change', function() {
                    console.log('Checkbox changed:', this.checked, 'Value:', this.value);
                    // Force update of visual state
                    this.nextElementSibling.classList.toggle('checked', this.checked);
                });
            });
        }

        /**
         * All events that are happening in form
         */
        events() {
            // Next step
            this.$next.forEach(btn => {
                btn.addEventListener("click", e => {
                    e.preventDefault();
                    this.currentStep++;
                    this.updateForm();
                });
            });

            // Previous step
            this.$prev.forEach(btn => {
                btn.addEventListener("click", e => {
                    e.preventDefault();
                    this.currentStep--;
                    this.updateForm();
                });
            });
        }

        /**
         * Update form front-end
         * Show next or previous section etc.
         */
        updateForm() {
            this.$step.innerText = this.currentStep;

            // TODO: Validation

            this.slides.forEach(slide => {
                slide.classList.remove("active");

                if (slide.dataset.step == this.currentStep) {
                    slide.classList.add("active");
                }
            });

            this.$stepInstructions[0].parentElement.parentElement.hidden = this.currentStep >= 5;
            this.$step.parentElement.hidden = this.currentStep >= 5;

            if (this.currentStep === 4) {
                this.setupAddressListeners();
            }
        }

        setupAddressListeners() {
            const addressInputsContainer = document.querySelector('.form-section--columns'); // Assuming this is your container
            const addressInputs = addressInputsContainer.querySelectorAll(
                'input[name="donation.street"], input[name="donation.city"], input[name="donation.zip"], input[name="donation.phone"], input[name="donation.pickUpDate"], input[name="donation.pickUpTime"], textarea[name="donation.pickUpComment"]'
            );

            console.log(addressInputs); // Check if elements are selected

            addressInputs.forEach(input => {
                const eventName = input.type === 'date' ? 'change' : 'input';
                input.addEventListener(eventName, this.updateDonationDelivery.bind(this));
            });
        }

        updateQuantity() {
            const quantity = document.querySelector('input[name="donation.quantity"]').value || 0;
            const quantitySummary = quantity > 0 ? `${quantity} ${quantity > 1 ? 'worki' : 'worek'}` : '';
            document.getElementById('donationSummary').textContent = quantitySummary;
        }

        updateDonationSummary() {
            const selectedCategories = Array.from(document.querySelectorAll('input[name="categories"]:checked'))
                .map(checkbox => checkbox.parentElement.nextElementSibling.textContent);
            console.log("selectedCategories: " + selectedCategories)
            const quantitySummary = document.getElementById('donationSummary').textContent;
            console.log("quantitySummary: " + quantitySummary)
            const summaryText = quantitySummary ? `${quantitySummary} ${selectedCategories.join(', ')}` : '';
            document.getElementById('donationSummary').textContent = summaryText;
            console.log("summary text: " + summaryText)
        }

        updateOrganizationSummary() {
            const selectedOrganization = document.querySelector('input[type="radio"][name="donation.institution"]:checked');
            const organizationSummary = selectedOrganization
                ? selectedOrganization.closest('label').querySelector('.title').textContent
                : '';
            document.getElementById('organizationSummary').textContent = `Dla ${organizationSummary}`;
        }

        updateDonationDelivery() {
            const updateField = (inputName, summaryId) => {
                const addressInputsContainer = document.querySelector('.form-section--columns');
                let inputElement = addressInputsContainer.querySelector(`input[name="${inputName}"]`);

                if (!inputElement) {
                    inputElement = addressInputsContainer.querySelector(`textarea[name="${inputName}"]`);
                }

                if (inputElement) {
                    let fieldValue = inputElement.value;

                    if (inputElement.type === 'date') {
                        const dateObject = new Date(fieldValue);
                        const year = dateObject.getFullYear();
                        const month = ("0" + (dateObject.getMonth() + 1)).slice(-2);
                        const day = ("0" + dateObject.getDate()).slice(-2);
                        fieldValue = `${year}-${month}-${day}`;
                    }

                    document.getElementById(summaryId).textContent = fieldValue;
                }
            };

            // Update all summary fields (make sure input names are consistent)
            updateField('donation.street', 'streetSummary');
            updateField('donation.city', 'citySummary');
            updateField('donation.zip', 'zipSummary');
            updateField('donation.phone', 'phoneSummary');

            updateField('donation.pickUpDate', 'pickUpDateSummary');
            updateField('donation.pickUpTime', 'pickUpTimeSummary');
            updateField('donation.pickUpComment', 'pickUpCommentSummary');
        }
    }

    const form = document.querySelector(".form--steps");
    if (form !== null) {
        new FormSteps(form);
    }
});