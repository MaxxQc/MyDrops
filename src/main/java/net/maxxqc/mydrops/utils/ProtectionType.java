package net.maxxqc.mydrops.utils;

public enum ProtectionType
{
    ITEM_DROP,
    BLOCK_BREAK,
    ITEM_FRAME_DROP,
    VEHICLE_DESTROY,
    HANGING_BREAK,
    ENTITY_KILL,
    PLAYER_DEATH,
    MYTHIC_MOBS;

    private final String value;

    ProtectionType()
    {
        this.value = this.name().toLowerCase().replace('_', '-');
    }

    public String getStringValue()
    {
        return this.value;
    }

    public static ProtectionType fromValue(String value)
    {
        return ProtectionType.valueOf(value.toUpperCase().replace('-', '_'));
    }
}
