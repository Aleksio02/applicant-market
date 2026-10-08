import type { UUID } from '@/shared/types/api'

export interface SkillItem {
  id: UUID
  code: string
  name: string
  category: string
}

export const SKILLS: SkillItem[] = [
  // BACKEND
  { id: 'bca62b92-2f9f-4aa8-81e9-d735f6ebc539', code: 'JAVA',        name: 'Java',        category: 'BACKEND' },
  { id: 'a7976759-b373-4205-87ff-1638fbcc669f', code: 'SPRING',      name: 'Spring',      category: 'BACKEND' },
  { id: '1cf827c5-2997-41dd-89df-3a127757e754', code: 'KOTLIN',      name: 'Kotlin',      category: 'BACKEND' },
  { id: '4f410bd6-0bc3-4d6d-b28e-73d2bdff9cd1', code: 'GO',          name: 'Go',          category: 'BACKEND' },
  { id: '50de1fc8-d096-472f-8d93-284dfe1b49fe', code: 'PYTHON',      name: 'Python',      category: 'BACKEND' },
  { id: '59052a1c-3d54-46df-bd95-d905193a7110', code: 'NODEJS',      name: 'Node.js',     category: 'BACKEND' },
  { id: '053a04ab-d134-4c8e-a61f-c7138f47415d', code: 'POSTGRESQL',  name: 'PostgreSQL',  category: 'BACKEND' },
  { id: 'b4acd286-784c-4d7b-b594-d2ee570c5cfc', code: 'MYSQL',       name: 'MySQL',       category: 'BACKEND' },
  { id: 'd6d969b9-98fd-4b4c-9789-ac77340a6a8e', code: 'MONGODB',     name: 'MongoDB',     category: 'BACKEND' },
  { id: '6960e800-a47f-4654-ad63-82e71b5708c2', code: 'REDIS',       name: 'Redis',       category: 'BACKEND' },
  { id: '9152cdb3-1615-4086-96ae-5f81ed76ae7e', code: 'KAFKA',       name: 'Kafka',       category: 'BACKEND' },
  { id: '974f6e12-32a8-4f1f-a230-0ab53cdebc13', code: 'RABBITMQ',    name: 'RabbitMQ',    category: 'BACKEND' },
  { id: 'fbf31a21-8ff8-47cf-ad4a-72a7493d3497', code: 'REST',        name: 'REST',        category: 'BACKEND' },
  { id: '5172bab1-b90c-4b76-9179-62ff7ac8bd78', code: 'GRAPHQL',     name: 'GraphQL',     category: 'BACKEND' },
  { id: '6c373aff-5a80-42ea-9bcf-03b15de0aaff', code: 'GRPC',        name: 'gRPC',        category: 'BACKEND' },

  // FRONTEND
  { id: '3395129d-74b8-4103-9de1-f7d38c7faf07', code: 'REACT',       name: 'React',       category: 'FRONTEND' },
  { id: 'b6920701-8b55-41ca-b6fb-e4734adea904', code: 'VUE',         name: 'Vue',         category: 'FRONTEND' },
  { id: 'ae40d659-38fd-47bd-9d0c-8729de9b23e0', code: 'ANGULAR',     name: 'Angular',     category: 'FRONTEND' },
  { id: '95c4d55b-9d90-4f36-ad7e-64f0365a7aab', code: 'TYPESCRIPT',  name: 'TypeScript',  category: 'FRONTEND' },
  { id: 'd2ecddbe-88c9-4eac-ba2e-a0874fa36716', code: 'JAVASCRIPT',  name: 'JavaScript',  category: 'FRONTEND' },
  { id: 'aa6a290d-fc68-4f4f-9a6a-d94fe1470218', code: 'HTML',        name: 'HTML',        category: 'FRONTEND' },
  { id: 'e4683b62-a74a-4b7a-bfe1-e06fb2c03f57', code: 'CSS',         name: 'CSS',         category: 'FRONTEND' },
  { id: '975cd4b7-b388-48d9-8a62-9183c2d3d1a6', code: 'SASS',        name: 'Sass',        category: 'FRONTEND' },
  { id: '6d71d531-e5bd-419c-acb4-f02f7a959527', code: 'WEBPACK',     name: 'Webpack',     category: 'FRONTEND' },
  { id: '5ed4e396-554d-48c9-ae54-625350acdca6', code: 'VITE',        name: 'Vite',        category: 'FRONTEND' },

  // MOBILE
  { id: '52d288a8-8eeb-4b5f-aea5-af2d34622466', code: 'KOTLIN_ANDROID', name: 'Kotlin (Android)', category: 'MOBILE' },
  { id: '6ba04411-844d-46c4-846a-7e4b38b810b5', code: 'SWIFT',       name: 'Swift',       category: 'MOBILE' },
  { id: '75375f5a-1fd2-4d6b-9678-7fc5affbcd89', code: 'FLUTTER',     name: 'Flutter',     category: 'MOBILE' },
  { id: '6c04b475-98d7-485d-84b2-349251a037ae', code: 'REACT_NATIVE', name: 'React Native', category: 'MOBILE' },

  // DEVOPS
  { id: '3099a8f4-5e98-4c99-9a32-c8c443566dc2', code: 'DOCKER',      name: 'Docker',      category: 'DEVOPS' },
  { id: '31726de2-a1a9-4534-b519-feab0698d094', code: 'KUBERNETES',  name: 'Kubernetes',  category: 'DEVOPS' },
  { id: '6b588b0b-2c1d-4712-9f9c-97f7ebcba128', code: 'JENKINS',     name: 'Jenkins',     category: 'DEVOPS' },
  { id: '14d16c90-d995-4cbd-aca6-456fde4146fc', code: 'GITLAB_CI',   name: 'GitLab CI',   category: 'DEVOPS' },
  { id: 'aac5e4ac-e5b4-4355-9ec5-1588fa498f56', code: 'GITHUB_ACTIONS', name: 'GitHub Actions', category: 'DEVOPS' },
  { id: 'af70d9d0-4821-48c7-a5a3-9704d38e79bb', code: 'TERRAFORM',   name: 'Terraform',   category: 'DEVOPS' },
  { id: '1cd235f4-a836-44aa-a0b0-69fd4e80ac87', code: 'ANSIBLE',     name: 'Ansible',     category: 'DEVOPS' },
  { id: '6f96d26f-c37f-43c1-9fca-80b1178ea21d', code: 'LINUX',       name: 'Linux',       category: 'DEVOPS' },
  { id: 'c4f81476-8463-4886-8c02-b19ad4280c7d', code: 'NGINX',       name: 'Nginx',       category: 'DEVOPS' },

  // DATA
  { id: 'b05919a3-8e55-4cb5-b916-d26b359eefbe', code: 'SQL',         name: 'SQL',         category: 'DATA' },
  { id: '9ea658df-96eb-4ca9-927f-b7d652e1f6c8', code: 'PYTORCH',     name: 'PyTorch',     category: 'DATA' },
  { id: 'f1dde846-012f-4d18-91e4-a2fd8b30e50c', code: 'TENSORFLOW',  name: 'TensorFlow',  category: 'DATA' },
  { id: '590eec4a-ac23-42ec-bb1f-d4fbf9498a0d', code: 'PANDAS',      name: 'Pandas',      category: 'DATA' },
  { id: 'v-nu-01',                              code: 'NUMPY',       name: 'NumPy',       category: 'DATA' },
  { id: 'v-sp-01',                              code: 'SPARK',       name: 'Apache Spark', category: 'DATA' },
  { id: 'v-ha-01',                              code: 'HADOOP',      name: 'Hadoop',      category: 'DATA' },
  { id: 'v-ai-01',                              code: 'AIRFLOW',     name: 'Airflow',     category: 'DATA' },

  // QA
  { id: 'v-se-01',                              code: 'SELENIUM',    name: 'Selenium',    category: 'QA' },
  { id: 'v-ju-01',                              code: 'JUNIT',       name: 'JUnit',       category: 'QA' },
  { id: 'v-tn-01',                              code: 'TESTNG',      name: 'TestNG',      category: 'QA' },
  { id: 'v-py-01',                              code: 'PYTEST',      name: 'pytest',      category: 'QA' },
  { id: 'v-po-01',                              code: 'POSTMAN',     name: 'Postman',     category: 'QA' },
  { id: 'v-jm-01',                              code: 'JMETER',      name: 'JMeter',      category: 'QA' },
  { id: 'v-al-01',                              code: 'ALLURE',      name: 'Allure',      category: 'QA' },

  // TOOLING
  { id: 'v-gi-01',                              code: 'GIT',         name: 'Git',         category: 'TOOLING' },
  { id: 'v-ji-01',                              code: 'JIRA',        name: 'Jira',        category: 'TOOLING' },
  { id: 'v-co-01',                              code: 'CONFLUENCE',  name: 'Confluence',  category: 'TOOLING' },
  { id: 'v-ma-01',                              code: 'MAVEN',       name: 'Maven',       category: 'TOOLING' },
  { id: 'v-gr-01',                              code: 'GRADLE',      name: 'Gradle',      category: 'TOOLING' },
]