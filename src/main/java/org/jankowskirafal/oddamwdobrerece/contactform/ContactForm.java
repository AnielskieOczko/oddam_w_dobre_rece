package org.jankowskirafal.oddamwdobrerece.contactform;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contact_form_messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContactForm {

    @Id
    @GeneratedValue
    Long id;

    String name;
    String surname;
    String message;
}
