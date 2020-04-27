package MySql;

public enum MySqlCommand {
    CREATE_ITEM(
            "CREATE TABLE IF NOT EXISTS `items_lib` (" +
                    "`id` VARCHAR(100)," +
                    "`item` LONGBLOB," +
                    "PRIMARY KEY (`id`))"
    ),
    UPDATE_ITEM("update items_lib SET id=?,item=? WHERE id=?"),
    ADD_ITEM("INSERT INTO items_lib (id,item)values (?,?)"),
    CHECK_ITEM("SELECT * FROM items_lib where id=?"),
    CREATE_MAIL(
            "CREATE TABLE IF NOT EXISTS `player_mail` (" +
                    "`player` VARCHAR(100)," +
                    "`mail` LONGBLOB," +
                    "`time` VARCHAR(100)," +
                    "PRIMARY KEY (`player`))"
    ),
    UPDATE_MAIL("update player_mail SET player=?,mail=? WHERE player=?"),
    CHECK_PLAYER("SELECT * FROM player_mail where player=?"),
    ADD_PLAYER("INSERT INTO player_mail (player,mail,time)values (?,?,?)"),
    CHECK_MAIL("SELECT * FROM player_mail where player=?");
    private String command;
    MySqlCommand(String command){
        this.command = command;
    }
    public String getCommand(){
        return this.command;
    }
}
