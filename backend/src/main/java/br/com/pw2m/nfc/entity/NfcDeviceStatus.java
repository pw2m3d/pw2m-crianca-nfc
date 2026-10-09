package br.com.pw2m.nfc.entity;

public enum NfcDeviceStatus {
    //Fabricado pela PW2M, ainda sem comprador.
    AVAILABLE,
    //Já pertence a um cliente, mas ainda não foi vinculado a uma pessoa.
    CLAIMED,
    //Vinculado e funcionando normalmente.
    ACTIVE,
    //Temporariamente desativado pelo cliente.
    SUSPENDED,
    //Marcado como perdido.
    LOST,
    //Bloqueado administrativamente pela PW2M.
    BLOCKED

}