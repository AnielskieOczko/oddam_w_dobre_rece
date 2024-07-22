document.addEventListener("DOMContentLoaded", function () {

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
    document.addEventListener("click", function (e) {
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

            // Form submit
            // this.$form.querySelector("form").addEventListener("submit", e => this.submit(e));
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


            // Set up address input listeners when step 4 becomes active
            if (this.currentStep === 4) {
                this.setupAddressListeners();
            }

            // Update donation summary ONLY when step 1 is active (adjust step number if needed)
            if (this.currentStep === 1) {
                this.updateDonationSummary();
            }

            // Update organization summary ONLY when step 3 is active (adjust step number if needed)
            if (this.currentStep === 3) {
                this.updateOrganizationSummary();
            }
            if (this.currentStep === 2) {
                this.updateQuantity();
            }

            document.querySelectorAll('input[name="categories"], input[name="bags"]').forEach(input => {
                input.addEventListener('change', this.updateDonationSummary.bind(this)); // Use bind(this)!
            });

            document.querySelectorAll('input[type="radio"][name="donation.institution"]').forEach(input => {
                input.addEventListener('change', this.updateOrganizationSummary.bind(this));
            });

            document.querySelectorAll('input[type="number"][name="donation.quantity"]').forEach(input => {
                input.addEventListener('change', this.updateQuantity.bind(this));
            });
        }

        setupAddressListeners() {
            const addressInputs = this.$form.querySelectorAll(
                'input[name="street"], input[name="city"], input[name="postcode"], input[name="phone"], input[name="date"], input[name="time"], textarea[name="more_info"]'
            );

            console.log(addressInputs); // Check if elements are selected

            addressInputs.forEach(input => {
                const eventName = input.type === 'date' ? 'change' : 'input';
                input.addEventListener(eventName, this.updateDonationDelivery.bind(this));
            });
        }


        updateQuantity() {
            const selectedCategories = Array.from(document.querySelectorAll('input[name="categories"]:checked'))
                .map(checkbox => checkbox.parentElement.nextElementSibling.textContent);
            const quantity = document.querySelector('input[name="donation.quantity"]').value || 0;
            console.log(quantity)

            const summaryText = quantity > 0
                ? `${quantity} ${quantity > 1 ? 'worki' : 'worek'} ${selectedCategories.join(', ')}`
                : '';
            console.log(summaryText)
            document.getElementById('donationSummary').textContent = summaryText;
        }

        updateDonationSummary() {
            const selectedCategories = Array.from(document.querySelectorAll('input[name="categories"]:checked'))
                .map(checkbox => checkbox.parentElement.nextElementSibling.textContent);
        }

        updateOrganizationSummary() {
            // Select the checked radio button using its type and checked state
            const selectedOrganization = document.querySelector('input[type="radio"][name="donation.institution"]:checked');
            console.log("Selected Organization Element:", selectedOrganization); // Check if selected correctly

            // Get the title text from the parent label element
            const organizationSummary = selectedOrganization
                ? selectedOrganization.closest('label').querySelector('.title').textContent
                : '';
            console.log("Organization Summary Text:", organizationSummary); // Check extracted text

            document.getElementById('organizationSummary').textContent = `Dla ${organizationSummary}`;
        }




        updateDonationDelivery() {
            const updateField = (inputName, summaryId) => {
                let inputElement = this.$form.querySelector(`input[name="${inputName}"]`);

                if (!inputElement) {
                    inputElement = this.$form.querySelector(`textarea[name="${inputName}"]`);
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

            // Update all summary fields
            updateField('donation.street', 'streetSummary');
            updateField('donation.city', 'citySummary');
            updateField('donation.zip', 'zipSummary');
            updateField('donation.phone', 'phoneSummary');

            updateField('donation.date', 'pickUpDateSummary');
            updateField('donation.time', 'pickUpTimeSummary');
            updateField('donation.more_info', 'pickUpCommentSummary');
        }

    }

    const form = document.querySelector(".form--steps");
    if (form !== null) {
        new FormSteps(form);
    }
});
