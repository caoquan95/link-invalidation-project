package tm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class PropertyMapping {
    private Model model;
    private Map<String, Property> maps;

    public PropertyMapping(Model m, double threshold) {
        this.model = m;
        this.maps = filterFunctionalProps(this.extractProp(), threshold);

    }

    private Map<String, Property> extractProp() {
        StmtIterator itr = model.listStatements();
        Map<String, Property> m = new HashMap<>();
        while (itr.hasNext()) {
            Statement st = itr.nextStatement();

            String sub = st.getSubject().toString();
            String pred = st.getPredicate().toString();

            Property prop = m.get(pred);
            if (prop == null) {
                prop = new Property(pred);
                m.put(pred, prop);
            }
            prop.addSubject(sub);
        }

        return m;
    }

    private Map<String, Property> filterFunctionalProps(Map<String, Property> mp, double threshold) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Property prop = (Property) pair.getValue();
            prop.computeFunctionalDegree();
        }

        Map<String, Property> maps = mp.entrySet()
                .stream()
                .filter(x -> x.getValue().getFunctionalDegree() > threshold)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return maps;
    }

    public Map<String, Property> getMaps() {
        return maps;
    }
}
