package org.motechproject.decisiontree.server.web;

import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.impl.StdTypeResolverBuilder;
import org.codehaus.jackson.type.JavaType;
import org.motechproject.decisiontree.core.model.ITransition;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Prompt;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.core.repository.AllTrees;
import org.motechproject.decisiontree.server.service.DecisionTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Controller
public class DecisionTreeController {

    @Autowired
    private DecisionTreeService decisionTreeService;

    @Autowired
    private AllTrees allTrees;

    @RequestMapping(value = "/trees", method = RequestMethod.GET)
    @ResponseBody
    public List<Tree> getDecisionTrees() {
        return decisionTreeService.getDecisionTrees();
    }

    @RequestMapping(value = "/trees/{treeId}", method = RequestMethod.GET)
    @ResponseBody
    public Tree getTree(@PathVariable String treeId, HttpServletResponse response) throws IOException {
        return decisionTreeService.getDecisionTree(treeId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/trees/create", method = RequestMethod.POST)
    public void saveTree(@RequestBody Tree tree) throws Exception {
        decisionTreeService.saveDecisionTree(tree);
    }

    public static class DecisionTreeCustomTypeResolver extends StdTypeResolverBuilder {

        @Override
        public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes, BeanProperty property) {
            return shouldIncludeTypeInfoFor(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes, property) : null;
        }

        private boolean shouldIncludeTypeInfoFor(JavaType t) {
            return t.hasRawClass(Prompt.class) || t.hasRawClass(ITransition.class) || t.hasRawClass(Node.class);
        }

        @Override
        public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes, BeanProperty property) {
            return shouldIncludeTypeInfoFor(baseType) ? super.buildTypeSerializer(config, baseType, subtypes, property) : null;
        }
    }
}
