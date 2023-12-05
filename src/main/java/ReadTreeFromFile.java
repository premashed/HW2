import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReadTreeFromFile {
    private DatabaseConnection Connection;
    public void setConnection(DatabaseConnection conn) {
        this.Connection = conn;
    }
    public List<Tree> makeTreeList() throws SQLException {
        List<Tree> treeList = new ArrayList<>();
        try (java.sql.Connection connection = Connection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM TREES")) {
            while (resultSet.next()) {
                int nodeId = resultSet.getInt("id");
                int parentNodeId = resultSet.getInt("parent_id");
                Node node = new Node(nodeId);
                if (nodeId == parentNodeId) {
                    node.setParent(null); // создание нового дерева из корня
                    Tree Newtree = new Tree(node);
                    treeList.add(Newtree);
                } else {
                    for (Tree tree : treeList) {
                        Node rootNode = tree.getRoot();
                        Node parent = findNodeById(rootNode, parentNodeId); // поиск родителя в каждом дереве
                        if (parent != null) {
                            node.setParent(parent); // добавление при совпадении
                            parent.addChild(node);
                        }
                    }
                }
            }
        }
        return treeList;
    }

    private static Node findNodeById(Node node, int nodeId) {
        if (node.getId() == nodeId) {
            return node;
        }
        for (Node child : node.getChildren()) {
            Node result = findNodeById(child, nodeId);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
