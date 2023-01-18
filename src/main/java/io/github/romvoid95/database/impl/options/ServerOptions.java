package io.github.romvoid95.database.impl.options;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.github.romvoid95.commands.core.RoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ServerOptions
{
    private List<String> serverPrefixes                       = new LinkedList<>(Arrays.asList(">>"));
    private List<String> serverAdminRoles                     = new LinkedList<>();
    private List<String> serverModeratorRoles                 = new LinkedList<>();

    public void setRoleList(List<String> newRoleList, RoleType roleType)
    {
        switch (roleType) {
            case ADMIN -> 
            this.serverAdminRoles = newRoleList;
            case MOD -> 
            this.serverModeratorRoles = newRoleList;
            default ->
            throw new IllegalArgumentException("Unexpected value: " + roleType);
        }
    }
    
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
