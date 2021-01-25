package br.com.asap.api.config.jackson;

public class JsonViews {

    public interface CreatedAt {}
    public interface UpdatedAt {}
    public interface Id {}
    public interface Show extends Id, CreatedAt, UpdatedAt {}

    public interface Create extends Id {}
    public interface Update extends Id {}

    public interface ShowPolicy extends Show {}

}
