package com.readonlydev.core;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public enum CommandPermission {
    USER() {
        @Override
        public boolean test(Member member) {
            return true;
        }
    },
    ADMIN() {
        @Override
        public boolean test(Member member) {
            return member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR) ||
                    member.hasPermission(Permission.MANAGE_SERVER) || OWNER.test(member) ||
                    member.getRoles().stream().map(r -> r.getId()).anyMatch(Accessors.staffRoles().getAdmins()::contains);
        }
    },
    OWNER() {
        @Override
        public boolean test(Member member) {
            return Accessors.botInfo().isOwner(member);
        }
    };

    public abstract boolean test(Member member);

    @Override
    public String toString() {
        String name = name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
