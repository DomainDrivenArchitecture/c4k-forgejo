from os import environ
from pybuilder.core import task, init
from ddadevops import *
import logging

name = 'c4k-gitea-backup'
MODULE = 'docker'
PROJECT_ROOT_PATH = '../..'

class MyBuild(DevopsDockerBuild):
    pass

@init
def initialize(project):
    project.build_depends_on('ddadevops>=0.12.4')
    stage = 'prod'
    dockerhub_user = environ.get('DOCKERHUB_USER')
    if not dockerhub_user:
        dockerhub_user = gopass_field_from_path('meissa/web/docker.com', 'login')
    dockerhub_password = environ.get('DOCKERHUB_PASSWORD')
    if not dockerhub_password:
        dockerhub_password = gopass_password_from_path('meissa/web/docker.com')
    tag = environ.get('CI_COMMIT_TAG')
    if not tag:
        tag = get_tag_from_latest_commit()
    config = create_devops_docker_build_config(
        stage, PROJECT_ROOT_PATH, MODULE, dockerhub_user, dockerhub_password, docker_publish_tag=tag)
    build = MyBuild(project, config)
    build.initialize_build_dir()


@task
def image(project):
    build = get_devops_build(project)
    build.image()

@task
def drun(project):
    build = get_devops_build(project)
    build.drun()

@task
def publish(project):
    build = get_devops_build(project)
    build.dockerhub_login()
    build.dockerhub_publish()

@task
def test(project):
    build = get_devops_build(project)
    build.test()
