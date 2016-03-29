package org.elasticsearch.plugin.readonlyrest.acl.blocks.rules.impl;

import com.google.common.collect.Lists;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugin.readonlyrest.ConfigurationHelper;
import org.elasticsearch.plugin.readonlyrest.acl.RequestContext;
import org.elasticsearch.plugin.readonlyrest.acl.blocks.rules.Rule;
import org.elasticsearch.plugin.readonlyrest.acl.blocks.rules.RuleExitResult;
import org.elasticsearch.plugin.readonlyrest.acl.blocks.rules.RuleNotConfiguredException;

import java.util.List;

/**
 * Created by sscarduzio on 20/02/2016.
 */
public class IndicesRule extends Rule {

  private final static ESLogger logger = Loggers.getLogger(IndicesRule.class);

  protected List<String> indicesToMatch;

  public IndicesRule(Settings s) throws RuleNotConfiguredException {
    super(s);
    String[] a = s.getAsArray(KEY);
    if (a != null && a.length > 0) {
      indicesToMatch = Lists.newArrayList();
      for (int i = 0; i < a.length; i++) {
        if (!ConfigurationHelper.isNullOrEmpty(a[i])) {
          indicesToMatch.add(a[i].trim());
        }
      }
    } else {
      throw new RuleNotConfiguredException();
    }
  }

  @Override
  public RuleExitResult match(RequestContext rc) {
    String[] indices = rc.getIndices();
    if (indices == null || indices.length == 0) {
      logger.warn("didn't find any index for this request: " + rc.getRequest().method() + " " + rc.getRequest().rawPath());
      return NO_MATCH;
    }
    for (String requiredIndex : indices) {
      if (!indicesToMatch.contains(requiredIndex)) {
        logger.debug("This request uses the index '" + requiredIndex + "' which is not in the list.");
        return NO_MATCH;
      }
    }
    return MATCH;
  }
}
