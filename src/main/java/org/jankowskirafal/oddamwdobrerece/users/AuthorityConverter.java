package org.jankowskirafal.oddamwdobrerece.users;

import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthorityConverter implements Converter<String, Authority> {

    private AuthorityRepository authorityRepository;

    @Override
    public Authority convert(String source) {
        return authorityRepository.findByName(source)
                .orElseThrow(() -> new IllegalArgumentException("Authority not found for name: " + source));
    }
}
