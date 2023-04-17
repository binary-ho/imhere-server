package gdsc.binaryho.imhere.domain.Infrastructure;

import gdsc.binaryho.imhere.domain.member.Role;
import gdsc.binaryho.imhere.domain.member.Roles;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RolesConverter implements AttributeConverter<Roles, String> {

    @Override
    public String convertToDatabaseColumn(Roles attribute) {
        if (attribute == null) {
            return null;
        }

        String roleString = attribute.getRoles().toString();
        return roleString.substring(1, roleString.length() - 1);
    }

    @Override
    public Roles convertToEntityAttribute(String dbData) {
        List<Role> roleList = Arrays.stream(dbData.split(", "))
            .map(Role::valueOf)
            .collect(Collectors.toList());
        return new Roles(EnumSet.copyOf(roleList));
    }
}