-- project
INSERT INTO `project` (name, war_name, deploy_script_type) VALUES
('as-web', 'ajaxablesky', 0),
('as-passport', 'ableskypassport', 0),
('as-search', 'ableskysearch', 0),
('as-cms', 'ableskycms', 0),
('as-ad', 'ableskyadvertisement', 0),
('as-im', 'ableskyim', 0),
('as-mobile', 'ableskymobile', 0);

-- user
insert into `user` (username, password, create_time, update_time) values
('zyang', 'bdf1d39d28fa4d1f8952787bb18590d4', now(), now()),
('zygao', 'cd0a60267346d8c5802e449d02199a98', now(), now()),
('hdu', 'e35be519d0c391dd69f1b2afcda38130', now(), now()),
('gwang', '563ac502f5fbc231ff34a853ceac1d2e', now(), now());